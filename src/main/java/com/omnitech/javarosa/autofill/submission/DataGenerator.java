package com.omnitech.javarosa.autofill.submission;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormAutoFill;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DataGenerator {

    private static Logger LOG = Logger.getLogger(DataGenerator.class.getName());

    private String formDefXMl;
    private String username;
    private String password;
    private String serverUrl;
    private int    numberOfItems;
    private boolean initilized = false;


    private boolean dryRun = false;

    private JavarosaSubmitter submitter;

    public void start() {
        init();

        IntStream.rangeClosed(1, numberOfItems).forEach(this::generateAndMayBeSubmit);
    }

    private void generateAndMayBeSubmit(int iteration) {
        LOG.info("Generating Form: " + iteration);

        FormAutoFill formAutoFill = FormAutoFill.fromXml(formDefXMl);


        String submissionXml = formAutoFill.autoFill().getSubmissionXml();

        if (dryRun) {
            System.out.println("Generated: " + submissionXml);
        } else {
            try {
                submitter.submit(submissionXml);
            } catch (IOException e) {
                throw new AutoFillException(e);
            }
        }

    }


    private void init() {
        if (initilized) return;

        submitter = new JavarosaSubmitter().setServerUrl(serverUrl)
                                           .setUsername(username)
                                           .setPassword(password);


        initilized = true;
    }


    public DataGenerator setFormDefXMl(String formDefXMl) {
        this.formDefXMl = formDefXMl;
        return this;
    }

    public DataGenerator setUsername(String username) {
        this.username = username;
        return this;
    }

    public DataGenerator setPassword(String password) {
        this.password = password;
        return this;
    }

    public DataGenerator setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public DataGenerator setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
        return this;
    }

    public DataGenerator setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }
}
