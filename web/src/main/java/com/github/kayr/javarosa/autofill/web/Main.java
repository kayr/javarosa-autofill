package com.github.kayr.javarosa.autofill.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.kayr.javarosa.autofill.api.FormUtils;
import com.github.kayr.javarosa.autofill.submission.DataGenerator;
import com.github.kayr.javarosa.autofill.submission.JavarosaClient;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;

import static spark.Spark.*;

@SuppressWarnings("WeakerAccess")
public class Main {

    static EventsWebSocket eventSocket;
    static Logger          LOG      = org.slf4j.LoggerFactory.getLogger(Main.class);
    static ExecutorService e        = Executors.newCachedThreadPool();
    static Parser          parser   = Parser.builder().build();
    static HtmlRenderer    renderer = HtmlRenderer.builder().build();

    public static void main(String[] args) throws IOException {

        LogManager manager = LogManager.getLogManager();
        manager.readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.github");
        logger.setLevel(Level.ALL);

        Spark.staticFiles.location("/web");

        //Spark.staticFiles.externalLocation("C:\\var\\code\\prsnl\\javarosa-autofill\\javarosa-autofil-api\\web\\src\\main\\resources\\web");

        webSocket("/events", EventsWebSocket.class);

        after((request, response) -> response.header("Content-Encoding", "gzip"));

        post("/formList", Main::processFormList);

        post("/formProperties", Main::getPropertyFile);

        post("/generateData", Main::generateData);


        launch(String.format("http://localhost:%s/index.html", Spark.port()));
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

    private static Object getPropertyFile(Request req, Response res) {
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

    private static String parseMarkDown(IFormElement c) {
        String labelText = DataGenerator.getLabelText(c);
        try {
            Node node = parser.parse(labelText);
            return Jsoup.clean(renderer.render(node), Whitelist.basic());
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

            int        numberOfItem   = Math.min(reqData.getInt("numberOfItems", 10), 20);
            boolean    dryRyn         = reqData.getBoolean("dryRun", true);
            String     downloadUrl    = reqData.get("downloadUrl").asString();
            String     username       = reqData.getString("userId", "");
            String     xform          = withJRClient(reqData, jr -> jr.pullXform(downloadUrl));

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
                    generator.start();
                }
                catch (Exception x) {
                    LOG.error("Failed To Generate Data: ", x);
                    eventSocket.log(username, x.getMessage());
                }
            });
            res.type("text/plain");
            return DataGenerator.createPropertiesText(xform);

        });
    }


    private static <T, R> R withJRClient(JsonObject req, JRFunction<JavarosaClient, R> function) throws Exception {
        JavarosaClient javarosaClient = getJavarosaClient(req);
        return function.apply(javarosaClient);
    }

    private static JavarosaClient getJavarosaClient(JsonObject req) {
        return new JavarosaClient().setPassword(req.get("username").asString())
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
            }
            else {
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
            }
        }
    }


    private interface JRFunction<T, R> {
        R apply(T item) throws Exception;
    }

}
