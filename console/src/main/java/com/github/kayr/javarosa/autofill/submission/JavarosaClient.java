package com.github.kayr.javarosa.autofill.submission;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import okhttp3.*;
import org.joox.Match;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.joox.JOOX.$;

public class JavarosaClient {

    public static final String NAME_XML_SUBMISSION_FILE = "xml_submission_file";
    private             String serverUrl                = "http://localhost:8080/oxd/mpsubmit/odk";
    private             String username                 = "admin";
    private             String password                 = "admin";

    private OkHttpClient httpClient;

    public String getServerUrl() {
        return serverUrl;
    }

    public JavarosaClient setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public JavarosaClient setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JavarosaClient setPassword(String password) {
        this.password = password;
        return this;
    }


    public void submit(String payload) throws IOException {
        Map<String, Object> m = new HashMap<>();
        m.put(NAME_XML_SUBMISSION_FILE, payload);
        submit(m);
    }

    public void submit(Map<String, Object> data) throws IOException {
        init();
        // req.setHeader(DATE_HEADER, DateFormat.format("E, dd MMM yyyy hh:mm:ss zz", g)

        MultipartBody.Builder dataBuilder = createBodyBuilder(data);

        Request build = buildRequest().url(submitUrl())
                                      .post(dataBuilder.build())
                                      .build();


        Call call = httpClient.newCall(build);

        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed Submission: Message(" + response.message() + "): Body:" + response.body().string());
        }

    }

    private MultipartBody.Builder createBodyBuilder(Map<String, Object> data) {

        MultipartBody.Builder dataBuilder = new MultipartBody.Builder();

        data.forEach((k, v) -> {
            if (v instanceof String) {
                dataBuilder.addFormDataPart(k, k, RequestBody.create(MediaType.parse("application/xml"), (String) v));
            } else if (v instanceof byte[]) {
                dataBuilder.addFormDataPart(k, k, RequestBody.create(MediaType.parse("application/binary"), (byte[]) v));
            } else {
                throw new AutoFillException("Could not upload data of type: " + v.getClass());
            }
        });

        return dataBuilder;
    }

    private Request.Builder buildRequest() {
        return new Request.Builder().addHeader("User-Agent", "Javarosa-AutoFill")
                                    .addHeader("X-OpenRosa-Version", "1.0");
    }

    public List<XForm> formList() throws IOException, ParserConfigurationException, SAXException {
        Request build = buildRequest().url(serverUrl + "/formList")
                                      .build();

        Response response = httpClient.newCall(build).execute();

        if (!response.isSuccessful()) {
            throw new AutoFillException("HTTP Call Failed: " + response.toString());
        }

        String xmlResponse = response.body().string();


        return parseXform(xmlResponse);
    }

    public String pullXfom(XForm form) throws IOException {
        Request build = buildRequest().url(form.downloadUrl).build();

        Call call = httpClient.newCall(build);

        return call.execute().body().string();

    }

    private List<XForm> parseXform(String xForm) throws SAXException, IOException, ParserConfigurationException {

        return StreamSupport.stream(parseXml(xForm).find("xform").spliterator(), false)
                            .map(e -> {
                                XForm x = new XForm();
                                x.formID = $(e).find("formID").text();
                                x.name = $(e).find("name").text();
                                x.downloadUrl = $(e).find("downloadUrl").text();
                                x.hash = $(e).find("hash").text();
                                return x;
                            })
                            .collect(Collectors.toList());


    }

    private Match parseXml(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        dBuilder  = dbFactory.newDocumentBuilder();
        ByteArrayInputStream   r         = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        Document               doc       = dBuilder.parse(r);
        return $(doc);
    }


    public static class XForm {
        String formID, name, downloadUrl, hash;

        @Override
        public String toString() {
            return "XForm{" +
                    "formID='" + formID + '\'' +
                    ", name='" + name + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    ", hash='" + hash + '\'' +
                    '}';
        }

        String getName_Id() {
            return name + "_" + formID;
        }
    }

    private String submitUrl() {
        return serverUrl + "/submission";
    }


    private boolean initialized = false;

    public synchronized JavarosaClient reInit() {
        initialized = false;
        return init();
    }

    private synchronized JavarosaClient init() {

        if (!initialized) {
            httpClient =
                    new OkHttpClient.Builder()
                            .addInterceptor(new BasicAuthInterceptor(username, password))
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build();

            if (serverUrl.endsWith("/")) {
                serverUrl = serverUrl.substring(0, serverUrl.length() - 1);

            }
            initialized = true;
        }

        return this;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }


}
