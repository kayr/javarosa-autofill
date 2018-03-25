package com.github.kayr.javarosa.autofill.submission;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.FormAutoFill;
import com.github.kayr.javarosa.autofill.api.FormUtils;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IDataReference;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.core.services.transport.payload.DataPointerPayload;
import org.javarosa.core.services.transport.payload.IDataPayload;
import org.javarosa.core.services.transport.payload.MultiMessagePayload;
import org.javarosa.xpath.XPathConditional;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataGenerator {

    private static Logger LOG = Logger.getLogger(DataGenerator.class.getName());

    private String              formDefXMl;
    private String              username;
    private String              password;
    private String              serverUrl;
    private int                 numberOfItems;
    private Listener            dataListener;
    private Map<String, String> generexMap;
    private boolean initilized = false;
    private boolean dryRun     = false;
    private JavarosaClient javarosaClient;

    public static String createPropertiesText(String xform) {
        FormDef formDef = FormUtils.parseFromText(xform);

        List<IFormElement> children = FormUtils.getChildren(formDef);

        List<String> bindVariables = children.stream()
                                             .filter(c -> c.getBind() != null && c.getBind().getReference().toString().lastIndexOf('/') != 0)
                                             .map(e -> createPropertiesFileLine(formDef, e))
                                             .collect(Collectors.toList());

        return String.join("=\n\n", bindVariables) + "=";
    }

    private static String createPropertiesFileLine(FormDef formDef, IFormElement e) {
        Optional<String> xpathConstraint = getXpathConstraint(formDef, e);

        String constraintStr = xpathConstraint.map(s -> "# Constraint: " + xpathConstraint + "\n").orElse("");

        return "# " +
                getLabelText(e) +
                "\n" +
                constraintStr +
                FormUtils.resolveVariable(e);
    }

    public static Optional<String> getXpathConstraint(FormDef formDef, IFormElement e) {
        IDataReference        reference = FormUtils.getSafeXpathReference(formDef, (TreeReference) e.getBind().getReference());
        Optional<TreeElement> element   = FormUtils.getTreeElement(formDef, reference);
        if (element.isPresent()) {
            return getXpathConstraint(element.get());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getXpathConstraint(TreeElement element) {
        return Optional.ofNullable(element.getConstraint())
                       .map(c -> ((XPathConditional) c.constraint).xpath.replaceAll("\\s+", " "));
    }

    public static String getLabelText(IFormElement e) {
        return Optional.ofNullable(e.getLabelInnerText())
                       .orElse("")
                       .replaceAll("\\s+", " ");
    }

    public DataGenerator setGenerexMap(Map<String, String> generexMap) {
        this.generexMap = generexMap;
        return this;
    }

    public DataGenerator setDataListener(Listener dataListener) {
        this.dataListener = dataListener;
        return this;
    }

    public DataGenerator start() {
        init();

        IntStream.rangeClosed(1, numberOfItems).forEach(this::generateAndMayBeSubmit);

        return this;
    }

    private void generateAndMayBeSubmit(int iteration) {
        LOG.info("Generating Form: " + iteration);

        notifyDataStart(iteration);

        FormAutoFill formAutoFill = FormAutoFill.fromXml(formDefXMl);

        if (generexMap != null) {
            formAutoFill.addGenerex(generexMap);
        }

        IDataPayload payload = null;
        try {
            payload = formAutoFill.autoFill().getSubmissionPayload();
        }
        catch (IOException e) {
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

            notifyDataEnd(iteration, payloadMap);

            if (!dryRun) {
                notifySubmitStart(iteration, payloadMap);
                javarosaClient.submit(payloadMap);
                notifySubmitEnd(iteration, payloadMap);
            }

        }
        catch (IOException e) {
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

                }
                catch (IOException e) {
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

    private void notifyDataStart(int i) {
        if (dataListener != null) {
            dataListener.onDataStart(i);
        }
    }

    private void notifyDataEnd(int i, Map payload) {
        if (dataListener != null) {
            dataListener.onDataEnd(i, payload);
        }
    }

    private void notifySubmitStart(int i, Map payload) {
        if (dataListener != null) {
            dataListener.onSubmitStart(i, payload);
        }
    }

    private void notifySubmitEnd(int i, Map payload) {
        if (dataListener != null) {
            dataListener.onSubmitEnd(i, payload);
        }
    }

    public interface Listener {

        default void onDataStart(int i) {
        }


        default void onDataEnd(int i, Map payload) {
        }

        default void onSubmitStart(int i, Map payload) {
        }

        default void onSubmitEnd(int i, Map payload) {
        }
    }

}
