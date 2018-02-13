package com.github.kayr.javarosa.autofill.submission;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.FormAutoFill;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.core.services.transport.payload.DataPointerPayload;
import org.javarosa.core.services.transport.payload.IDataPayload;
import org.javarosa.core.services.transport.payload.MultiMessagePayload;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DataGenerator {

    private static Logger LOG = Logger.getLogger(DataGenerator.class.getName());

    private String                      formDefXMl;
    private String                      username;
    private String                      password;
    private String                      serverUrl;
    private int                         numberOfItems;
    private BiConsumer<Integer, String> dataListener;
    private Map<String, String>         generexMap;

    public DataGenerator setGenerexMap(Map<String, String> generexMap) {
        this.generexMap = generexMap;
        return this;
    }

    private boolean initilized = false;


    public DataGenerator setDataListener(BiConsumer<Integer, String> dataListener) {
        this.dataListener = dataListener;
        return this;
    }

    private boolean dryRun = false;

    private JavarosaClient javarosaClient;

    public void start() {
        init();

        IntStream.rangeClosed(1, numberOfItems).forEach(this::generateAndMayBeSubmit);
    }

    private void generateAndMayBeSubmit(int iteration) {
        LOG.info("Generating Form: " + iteration);

        FormAutoFill formAutoFill = FormAutoFill.fromXml(formDefXMl);

        if (generexMap != null) {
            formAutoFill.addGenerex(generexMap);
        }

        IDataPayload payload = null;
        try {
            payload = formAutoFill.autoFill().getSubmissionPayload();
        } catch (IOException e) {
            IOUtils.sneakyThrow(e);
        }


        try {

            Map<String, Object> payloadMap = new HashMap<>();


            if (payload instanceof ByteArrayPayload) {
                String payload1 = FormAutoFill.payloadToXml(payload);
                payloadMap.put(JavarosaClient.NAME_XML_SUBMISSION_FILE, payload1);


            } else if (payload instanceof MultiMessagePayload) {

                MultiMessagePayload multiMessagePayload = (MultiMessagePayload) payload;
                payloadMap = buildPayloadMap(multiMessagePayload);

            }

            if (dataListener != null) {
                dataListener.accept(iteration, payloadMap.get(JavarosaClient.NAME_XML_SUBMISSION_FILE).toString());
            }

            if (!dryRun) {
                javarosaClient.submit(payloadMap);
            }

        } catch (IOException e) {
            throw new AutoFillException(e);
        }

    }

    private Map<String, Object> buildPayloadMap(MultiMessagePayload multiMessagePayload) {

        Map<String, Object> payloadData = new HashMap<>();

        multiMessagePayload.getPayloads().forEach(pl -> {
            if (pl instanceof ByteArrayPayload) {

                payloadData.put(JavarosaClient.NAME_XML_SUBMISSION_FILE, FormAutoFill.payloadToXml(pl));

            } else if (pl instanceof DataPointerPayload) {
                try {
                    DataPointerPayload dataReference = (DataPointerPayload) pl;
                    InputStream        payloadStream = dataReference.getPayloadStream();
                    byte[]             bytes         = IOUtils.getBytes(payloadStream);

                    payloadData.put(dataReference.getPayloadId(), bytes);

                } catch (IOException e) {
                    IOUtils.sneakyThrow(e);
                }
            }
        });
        return payloadData;
    }


    private void init() {
        if (initilized) return;

        javarosaClient = new JavarosaClient().setServerUrl(serverUrl)
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
