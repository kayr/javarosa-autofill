package com.github.kayr.javarosa.autofill.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.kayr.javarosa.autofill.submission.JavarosaClient;
import org.xml.sax.SAXException;
import spark.Spark;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

@SuppressWarnings("WeakerAccess")
public class Main {

    public static void main(String[] args) {

        Spark.staticFiles.location("/web");


        post("/formList", (req, res) -> {
            res.status(200);
            res.type("application/json");
            return getFormList(req.body());
        });


        launch(String.format("http://localhost:%s/index.html", Spark.port()));
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


    static String getFormList(String data) throws ParserConfigurationException, SAXException, IOException {

        JsonObject req = Json.parse(data).asObject();

        List<JavarosaClient.XForm> xForms =
                new JavarosaClient().setPassword(req.get("username").asString())
                                    .setPassword(req.get("password").asString())
                                    .setServerUrl(req.get("url").asString())
                                    .formList();


        JsonArray jsonArray = xForms.stream()
                                    .map(xForm -> Json.object().add("name", xForm.name)
                                                      .add("formID", xForm.formID)
                                                      .add("downloadUrl", xForm.downloadUrl)
                                                      .add("hash", xForm.hash))
                                    .collect(Json::array, JsonArray::add, (a1, a2) -> a2.forEach(a1::add));

        return jsonArray.toString();


    }


}
