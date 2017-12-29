package com.omnitech.javarosa.autofill.submission;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormAutoFill;
import com.omnitech.javarosa.autofill.api.FormUtils;
import org.javarosa.core.model.FormDef;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DataGenerator {

    static Logger LOG = Logger.getLogger(DataGenerator.class.getName());

    private String formDefXMl;
    private String username;
    private String password;
    private String serverUrl;
    private int    numberOfItems;
    private boolean initilized = false;


    private boolean dryRun = false;

    private JavarosaSubmitter submitter;
    private FormDef           formDef;

    public void start() {
        init();

        IntStream.rangeClosed(1, numberOfItems).forEach(this::generateAndMayBeSubmit);
    }

    private void generateAndMayBeSubmit(int iteration) {
        LOG.info("Generating Form: " + iteration);

        FormAutoFill formAutoFill = new FormAutoFill(formDef);

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

        formDef = FormUtils.parseFromText(formDefXMl);


        initilized = true;
    }


    public String getFormDefXMl() {
        return formDefXMl;
    }

    public DataGenerator setFormDefXMl(String formDefXMl) {
        this.formDefXMl = formDefXMl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DataGenerator setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DataGenerator setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public DataGenerator setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public DataGenerator setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
        return this;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public DataGenerator setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }
}
