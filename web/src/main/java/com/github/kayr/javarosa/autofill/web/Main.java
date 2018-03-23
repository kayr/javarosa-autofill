package com.github.kayr.javarosa.autofill.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.FormUtils;
import com.github.kayr.javarosa.autofill.submission.DataGenerator;
import com.github.kayr.javarosa.autofill.submission.JavarosaClient;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.GroupDef;
import org.javarosa.core.model.IFormElement;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.awt.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.*;

@SuppressWarnings("WeakerAccess")
public class Main {

    static EventsWebSocket eventSocket;
    static Logger          LOG = org.slf4j.LoggerFactory.getLogger(Main.class);
    static ExecutorService e   = Executors.newCachedThreadPool();
//    static Parser          parser   = Parser.builder().build();
//    static HtmlRenderer    renderer = HtmlRenderer.builder().build();

    public static void main(String[] args) {


        Spark.staticFiles.location("/web");

        //Spark.staticFiles.externalLocation("C:\\var\\code\\prsnl\\javarosa-autofill\\javarosa-autofil-api\\web\\src\\main\\resources\\web");

        port(9000);

        webSocket("/events", EventsWebSocket.class);

        after((request, response) -> response.header("Content-Encoding", "gzip"));

        post("/formList", Main::processFormList);

        post("/formProperties", Main::getQuestionsJson);

        post("/generateData", Main::generateData);


        String host = "localhost";
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            host = localHost.getHostName();
        }
        catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        launch(String.format("http://%s:%s/index.html", host, Spark.port()));
    }

    private static Object processFormList(Request req, Response res) {
        return doSafely(res, () -> {
            String jsonFormList = getFormList(req.body());
            res.status(200);
            res.type("application/json");
            return jsonFormList;
        });
    }

    static String getFormList(String data) throws Exception {

        JsonObject req = Json.parse(data).asObject();

        List<JavarosaClient.XForm> xForms = withJRClient(req, JavarosaClient::formList);

        JsonArray jsonArray = xForms.stream()
                                    .map(xForm -> Json.object().add("name", xForm.name)
                                                      .add("formID", xForm.formID)
                                                      .add("downloadUrl", xForm.downloadUrl)
                                                      .add("hash", xForm.hash))
                                    .collect(Json::array, JsonArray::add, (a1, a2) -> a2.forEach(a1::add));

        return jsonArray.toString();


    }

    private static Object getQuestionsJson(Request req, Response res) {
        return doSafely(res, () -> {
            JsonObject reqData = Json.parse(req.body()).asObject();
            String     url     = reqData.get("downloadUrl").asString();
            String     xform   = withJRClient(reqData, jr -> jr.pullXform(url));
            FormDef    formDef = FormUtils.parseFromText(xform);
            JsonObject object  = formToJson(formDef);

            res.type("application/json");
            return object.toString();

        });
    }

    private static JsonObject formToJson(FormDef formDef) {
        List<IFormElement> children      = FormUtils.getChildren(formDef);
        JsonObject         object        = Json.object();
        JsonArray          questionArray = Json.array();
        children.stream()
                .filter(c -> c.getBind() != null && c.getBind().getReference().toString().lastIndexOf('/') != 0)
                .filter(c -> isRepeatGroup(c) || !isGroup(c))
                .map(c -> {
                    JsonObject qn = Json.object();
                    qn.add("questionLabel", parseMarkDown(c));
                    qn.add("questionId", FormUtils.resolveVariable(c));
                    Optional<String> xpathConstraint = DataGenerator.getXpathConstraint(formDef, c);
                    if (xpathConstraint.isPresent())
                        qn.add("constraint", xpathConstraint.orElse(null));

                    return qn;
                })
                .forEach(questionArray::add);
        object.add("questions", questionArray);
        return object;
    }

    private static boolean isRepeatGroup(IFormElement e) {
        return isGroup(e) && ((GroupDef) e).getRepeat();
    }

    private static boolean isGroup(IFormElement e) {
        return e instanceof GroupDef;
    }

    private static boolean isGroupOnly(IFormElement e) {
        return isGroup(e) && !isRepeatGroup(e);
    }

    private static String parseMarkDown(IFormElement c) {
        String labelText = DataGenerator.getLabelText(c).trim();

        if (isRepeatGroup(c)) {
            labelText = "[Repeat] " + labelText;
        }

        try {
            //Node node = parser.parse(labelText);
            //String html = renderer.render(node);
            return Jsoup.clean(labelText, Whitelist.basic());
        }
        catch (Exception ignore) {
            try {
                return Jsoup.clean(labelText, Whitelist.basic());
            }
            catch (Exception ignore2) {
                return labelText;
            }
        }
    }

    private static Object generateData(Request req, Response res) {
        return doSafely(res, () -> {
            JsonObject reqData = Json.parse(req.body()).asObject();

            int     numberOfItem = Math.min(reqData.getInt("numberOfItems", 10), 20);
            boolean dryRyn       = reqData.getBoolean("dryRun", true);
            String  downloadUrl  = reqData.get("downloadUrl").asString();
            String  username     = reqData.getString("userId", "");
            String  xform        = withJRClient(reqData, jr -> jr.pullXform(downloadUrl));

            JsonArray generex = reqData.get("generex").asArray();

            Map<String, String> map = new HashMap<>();
            generex.forEach(generexObj -> {
                JsonObject jsonObject = generexObj.asObject();
                String     questionId = jsonObject.get("questionId").asString();
                String     generexStr = jsonObject.getString("generex", "");

                map.put(questionId, generexStr);


            });


            DataGenerator generator = new DataGenerator().setUsername(reqData.get("username").asString())
                                                         .setPassword(reqData.get("password").asString())
                                                         .setServerUrl(reqData.get("url").asString())
                                                         .setDryRun(dryRyn)
                                                         .setFormDefXMl(xform)
                                                         .setGenerexMap(map)
                                                         .setNumberOfItems(numberOfItem)
                                                         .setDataListener((integer, s) -> eventSocket.log(username, "Processing: " + integer));

            e.submit(() -> {
                try {
                    Thread.sleep(2000);
                    eventSocket.log(username, "Starting Data Generation");
                    generator.start();
                }
                catch (AutoFillException x) {
                    LOG.error("Failed To Generate Data: ", x);
                    String s = "";
                    if (x.getElement() != null) {
                        s = String.format(": <a href='#%s' class='c-element-link'>Jump To Question</a>", FormUtils.resolveVariable(x.getElement()));
                    }
                    eventSocket.log(username, x.getMessage() + s);

                }
                catch (Exception x) {
                    LOG.error("Failed To Generate Data: ", x);
                    eventSocket.log(username, x.getMessage());
                }
            });
            res.type("text/plain");
            return "OK - Submitted";

        });
    }


    private static <T, R> R withJRClient(JsonObject req, JRFunction<JavarosaClient, R> function) throws Exception {
        JavarosaClient javarosaClient = getJavarosaClient(req);
        return function.apply(javarosaClient);
    }

    private static JavarosaClient getJavarosaClient(JsonObject req) {
        return new JavarosaClient().setUsername(req.get("username").asString())
                                   .setPassword(req.get("password").asString())
                                   .setServerUrl(req.get("url").asString());
    }

    private static Object doSafely(Response res, Callable callable) {
        try {
            return callable.call();
        }
        catch (Exception x) {
            LOG.error("Error processing request: ", x);
            String message = x.getMessage();
            if (message != null && message.contains("code=401")) {
                res.status(401);
                return "Access Denied";
            } else {
                res.status(500);
                return Optional.ofNullable(message).orElse(x.toString());
            }

        }
    }

    private static Object doSafely(Callable callable) {
        try {
            return callable.call();
        }
        catch (Exception x) {
            LOG.error("Error processing request: ", x);

        }
        return null;
    }

    static void launch(String url) {
        System.out.println("Visit: " + url);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            }
            catch (Exception ignored) {
                LOG.warn("Failed to launch browser: " + url);
            }
        } else {
            LOG.info("Visit: " + url);
        }
    }


    private interface JRFunction<T, R> {
        R apply(T item) throws Exception;
    }

}
