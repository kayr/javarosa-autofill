package com.github.kayr.javarosa.autofill.submission;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JavarosaSubmitter {

    private String serverUrl;
    private String username;
    private String password;

    private OkHttpClient httpClient;

    public String getServerUrl() {
        return serverUrl;
    }

    public JavarosaSubmitter setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public JavarosaSubmitter setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JavarosaSubmitter setPassword(String password) {
        this.password = password;
        return this;
    }


    public void submit(String payload) throws IOException {
        init();
        // req.setHeader(DATE_HEADER, DateFormat.format("E, dd MMM yyyy hh:mm:ss zz", g)
        Request build = new Request.Builder().addHeader("User-Agent", "Javarosa-AutoFill")
                                             .addHeader("X-OpenRosa-Version", "1.0")
                                             .url(submitUrl())
                                             .post(new MultipartBody.Builder()
                                                           .addFormDataPart("xml_submission_file", "xml_submission_file", RequestBody.create(MediaType.parse("application/xml"), payload))
                                                           .build())
                                             .build();


        Call call = httpClient.newCall(build);

        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed Submission: Message(" + response.message() + "): Body:" + response.body().string());
        }

    }

    private String submitUrl() {
        return serverUrl + "/submission";
    }


    private boolean initialized = false;

    private synchronized void init() {

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
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }


}
