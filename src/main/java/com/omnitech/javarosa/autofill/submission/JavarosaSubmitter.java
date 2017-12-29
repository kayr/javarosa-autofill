package com.omnitech.javarosa.autofill.submission;

import okhttp3.*;

import java.io.IOException;

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


    public boolean submit(String payload) throws IOException {
        init();
        // req.setHeader(DATE_HEADER,
        //                DateFormat.format("E, dd MMM yyyy hh:mm:ss zz", g)


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

        return true;
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
                            .authenticator((route, response) -> {
                                if (responseCount(response) >= 3) {
                                    return null; // If we've failed 3 times, give up. - in real life, never give up!!
                                }
                                String credential = Credentials.basic(username, password);
                                return response.request().newBuilder().header("Authorization", credential).build();
                            })
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
