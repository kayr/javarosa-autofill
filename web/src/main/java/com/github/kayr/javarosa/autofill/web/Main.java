package com.github.kayr.javarosa.autofill.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.kayr.javarosa.autofill.submission.DataGenerator;
import com.github.kayr.javarosa.autofill.submission.JavarosaClient;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;

import static spark.Spark.post;

@SuppressWarnings("WeakerAccess")
public class Main {

    public static void main(String[] args) {

//        Spark.staticFiles.location("/web");
        Spark.staticFiles.externalLocation("C:\\var\\code\\prsnl\\javarosa-autofill\\javarosa-autofil-api\\web\\src\\main\\resources\\web");

        post("/formList", Main::processFormList);

        post("/formProperties", Main::getPropertyFile);

        launch(String.format("http://localhost:%s/index.html", Spark.port()));
    }

    private static Object processFormList(Request req, Response res) throws Exception {
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

    private static Object getPropertyFile(Request req, Response res) throws Exception {
        return doSafely(res, () -> {
            JsonObject reqData = Json.parse(req.body()).asObject();
            String     url     = reqData.get("url").asString();
            String     xform   = withJRClient(reqData, jr -> jr.pullXform(url));
            res.type("text/plain");
            return DataGenerator.createPropertiesText(xform);

        });
    }


    private static <T, R> R withJRClient(JsonObject req, JRFunction<JavarosaClient, R> function) throws Exception {
        JavarosaClient javarosaClient = getJavarosaClient(req);
        try {
            return function.apply(javarosaClient);
        } finally {
            javarosaClient.shutDown();
        }
    }

    private static JavarosaClient getJavarosaClient(JsonObject req) {
        return new JavarosaClient().setPassword(req.get("username").asString())
                                   .setPassword(req.get("password").asString())
                                   .setServerUrl(req.get("url").asString());
    }

    private static Object doSafely(Response res, Callable callable) throws Exception {
        try {
            return callable.call();
        } catch (Exception x) {
            String message = x.getMessage();
            if (message != null && message.contains("code=401")) {
                res.status(401);
                return "Access Denied";
            }
            throw x;
        }
    }

    static void launch(String url) {
        System.out.println("Visit: " + url);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ignored) {
            }
        }
    }


    private interface JRFunction<T, R> {
        R apply(T item) throws Exception;
    }

}
