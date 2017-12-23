package com.omnitech.javarosa.console;

import com.omnitech.javarosa.console.providers.*;
import org.javarosa.core.model.*;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.model.xform.XFormSerializingVisitor;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.xform.util.XFormUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FormAutoFill {

    private static Logger LOG = Logger.getLogger(FormAutoFill.class.getName());
    private FormDef             formDef;
    private FormEntryModel      model;
    private FormEntryController fec;
    private Map<ControlDataTypeKey, IAnswerProvider> answerProviderMap = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    public FormAutoFill(FormDef formDef) {
        this.formDef = formDef;
        init();

    }

    static FormAutoFill fromXml(String xForm) throws IOException {
        return new FormAutoFill(FormUtils.parseFromText(xForm));
    }

    static FormAutoFill fromResource(String resource) {
        return new FormAutoFill(XFormUtils.getFormFromResource(resource));
    }

    private void init() {
        formDef.initialize(true, new InstanceInitializationFactory());

        // create FormEntryController from formdef
        model = new FormEntryModel(formDef);
        fec = new FormEntryController(model);
        fec.jumpToIndex(FormIndex.createBeginningOfFormIndex());


        //primitives
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_TEXT, TextProvider.class);
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_INTEGER, NumberProvider.class);
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_DECIMAL, DecimalProvider.class);
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_BOOLEAN, BooleanProvider.class);

        //dates
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_DATE, DateProvider.class);
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_DATE_TIME, DateTimeProvider.class);
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_TIME, TimeProvider.class);

        //location
        addProvider(Constants.CONTROL_INPUT, Constants.DATATYPE_GEOPOINT, GPSProvider.class);


        addProvider(Constants.CONTROL_TEXTAREA, Constants.DATATYPE_TEXT, TextProvider.class);
        addProvider(Constants.CONTROL_TEXTAREA, Constants.DATATYPE_TEXT, TextProvider.class);

        addProvider(Constants.CONTROL_SELECT_MULTI, Constants.DATATYPE_CHOICE_LIST, MultiSelectProvider.class);
        addProvider(Constants.CONTROL_SELECT_ONE, Constants.DATATYPE_CHOICE, SelectOneProvider.class);


    }

    @SuppressWarnings("WeakerAccess")
    public <T extends IAnswerProvider> void addProvider(int controlType, int dataType, Class<T> provider) {
        try {
            addProvider(controlType, dataType, provider.newInstance());
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void addProvider(int controlType, int dataType, IAnswerProvider provider) {
        answerProviderMap.put(ControlDataTypeKey.with(controlType, dataType), provider);
    }


    FormAutoFill autoFill() {
        while (!isEndOfForm()) {
            next();
        }
        return this;
    }


    private void next() {
        int event = fec.stepToNextEvent();//model.getEvent(currentIdx);

        switch (event) {
            case FormEntryController.EVENT_BEGINNING_OF_FORM:
                LOG.fine("----------Form Begin");
                break;
            case FormEntryController.EVENT_END_OF_FORM:
                LOG.fine("----------Form End");
                break;
            case FormEntryController.EVENT_GROUP:
                LOG.fine("----------Form Group");
                break;
            case FormEntryController.EVENT_PROMPT_NEW_REPEAT:
                LOG.fine("----------Form New Repeat");
                boolean randomBoolean = FormUtils.randomBoolean();
                if (randomBoolean) {
                    LOG.info("Adding new repeat");
                    fec.newRepeat();
                }
                break;
            case FormEntryController.EVENT_QUESTION:
                handleQuestion();
                break;
            case FormEntryController.EVENT_REPEAT:
                LOG.fine("----------Form Repeat");
                break;
            case FormEntryController.EVENT_REPEAT_JUNCTURE:
                LOG.fine("----------Form EVENT_REPEAT_JUNCTURE");
                break;
        }


    }

    public boolean hasNext() {
        return !isEndOfForm();
    }

    @SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
    public boolean isEndOfForm() {
        return fec.getModel().getEvent() == FormEntryController.EVENT_END_OF_FORM;
    }

    private void handleQuestion() {

        FormEntryPrompt questionPrompt = model.getQuestionPrompt();

        LOG.fine(questionPrompt.getQuestionText());

        IAnswerProvider answerProvider = resolveProvider(questionPrompt);

        fec.answerQuestion(currentIndex(), answerProvider.acquire(fec, questionPrompt), true);

    }

    @SuppressWarnings("WeakerAccess")
    protected IAnswerProvider resolveProvider(FormEntryPrompt prompt) {
        IAnswerProvider iAnswerProvider = answerProviderMap.get(ControlDataTypeKey.fromPrompt(prompt));

        if (iAnswerProvider == null) {
            iAnswerProvider = answerProviderMap.get(ControlDataTypeKey.with(Constants.CONTROL_INPUT, Constants.DATATYPE_TEXT));
        }


        return iAnswerProvider;
    }

    private FormIndex currentIndex() {
        return model.getFormIndex();
    }


    @SuppressWarnings("WeakerAccess")
    public ByteArrayPayload getSubmissionPayload() throws IOException {
        FormInstance            instance   = getInstance();
        XFormSerializingVisitor serializer = new XFormSerializingVisitor();
        return (ByteArrayPayload) serializer.createSerializedPayload(instance, getSubmissionDataReference());
    }

    /**
     * Find the portion of the form that is to be submitted
     */
    private IDataReference getSubmissionDataReference() {
        FormDef formDef = fec.getModel().getForm();
        // Determine the information about the submission...
        SubmissionProfile p = formDef.getSubmissionProfile();
        if (p == null || p.getRef() == null) {
            return new XPathReference("/");
        } else {
            return p.getRef();
        }
    }

    private FormInstance getInstance() {
        return fec.getModel().getForm().getInstance();
    }

    public String getSubmissionXml() throws IOException {
        InputStream payloadStream = getSubmissionPayload().getPayloadStream();
        return IOUtils.getText(payloadStream);
    }

}