package com.omnitech.javarosa.console;

import com.omnitech.javarosa.console.functions.Fakers;
import com.omnitech.javarosa.console.functions.RandomRegex;
import com.omnitech.javarosa.console.functions.RandomSelectFromFile;
import com.omnitech.javarosa.console.providers.*;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.model.instance.TreeElement;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormAutoFill {

    private static Logger LOG = Logger.getLogger(FormAutoFill.class.getName());

    private FormDef             formDef;
    private FormEntryModel      model;
    private FormEntryController fec;

    private Map<ControlDataTypeKey, IAnswerProvider> answerProviderMap = new HashMap<>();
    private IAnswerProvider                          generexProvider   = new GenerexProvider();

    @SuppressWarnings("WeakerAccess")
    public FormAutoFill(FormDef formDef) {
        this.formDef = formDef;
        init();

    }

    public static FormAutoFill fromXml(String xForm) throws IOException {
        return new FormAutoFill(FormUtils.parseFromText(xForm));
    }

    public static FormAutoFill fromResource(String resource) {
        return new FormAutoFill(XFormUtils.getFormFromResource(resource));
    }

    private void init() {
        formDef.initialize(true, new InstanceInitializationFactory());
        model = new FormEntryModel(formDef);
        fec = new FormEntryController(model);
        fec.jumpToIndex(FormIndex.createBeginningOfFormIndex());

        initAnswerProviders();

        initFunctionHandlers();
    }

    private void initAnswerProviders() {
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

    private void initFunctionHandlers() {
        EvaluationContext ec = formDef.getEvaluationContext();

        ec.addFunctionHandler(new RandomRegex());
        ec.addFunctionHandler(new RandomSelectFromFile());

        Fakers.registerAll(formDef);
    }

    @SuppressWarnings("WeakerAccess")
    public <T extends IAnswerProvider> void addProvider(int controlType, int dataType, Class<T> provider) {
        try {
            addProvider(controlType, dataType, provider.newInstance());
        } catch (Exception x) {
            throw new AutoFillException(x);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void addProvider(int controlType, int dataType, IAnswerProvider provider) {
        answerProviderMap.put(ControlDataTypeKey.with(controlType, dataType), provider);
    }


    FormAutoFill autoFill() {
        while (!isEndOfForm()) {
            nextEvent();
        }

        ValidateOutcome validate = formDef.validate(true);

        if (validate != null) {
            FormEntryPrompt questionPrompt = fec.getModel().getQuestionPrompt(validate.failedPrompt);
            IAnswerData     answer         = questionPrompt.getAnswerValue();
            throw new IllegalArgumentException("Invalid Answer[" + answer.getValue() + "] For Question[" + questionPrompt.getQuestion().getLabelInnerText() + "]");

        }

        return this;
    }


    private void nextEvent() {
        int event = fec.stepToNextEvent();//model.getEvent(currentIdx);

        switch (event) {
            case FormEntryController.EVENT_BEGINNING_OF_FORM:
                LOG.info("Event: -> Form Begin");
                break;
            case FormEntryController.EVENT_END_OF_FORM:
                LOG.info("Event: -> Form End");
                break;
            case FormEntryController.EVENT_GROUP:
                LOG.finer("----------Form Group");
                break;
            case FormEntryController.EVENT_PROMPT_NEW_REPEAT:
                LOG.finer("----------Form New Repeat");
                boolean randomBoolean = FormUtils.randomBoolean();
                if (randomBoolean) {
                    LOG.fine("Adding new repeat");
                    fec.newRepeat();
                }
                break;
            case FormEntryController.EVENT_QUESTION:
                handleQuestion();
                break;
            case FormEntryController.EVENT_REPEAT:
                LOG.finer("----------Form Repeat");
                break;
            case FormEntryController.EVENT_REPEAT_JUNCTURE:
                LOG.finer("----------Form EVENT_REPEAT_JUNCTURE");
                break;
        }


    }


    @SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
    public boolean isEndOfForm() {
        return fec.getModel().getEvent() == FormEntryController.EVENT_END_OF_FORM;
    }

    private void handleQuestion() {

        FormEntryPrompt questionPrompt = model.getQuestionPrompt();
        IAnswerProvider answerProvider;

        if (hasGenerex(questionPrompt)) {
            answerProvider = generexProvider;
        } else {
            answerProvider = resolveProvider(questionPrompt);
        }

        IAnswerData answer = answerProvider.acquire(fec, questionPrompt);
        int         status = fec.answerQuestion(currentIndex(), answer, true);

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Answer for Question[" + questionPrompt.getQuestion().getBind().getReference() + "] = " + answer.getValue());
        }

        if (status != FormEntryController.ANSWER_OK)
            throw new IllegalArgumentException("Invalid Answer[" + answer.getValue() + "] For Question[" + questionPrompt.getQuestion().getBind().getReference() + "]");


    }

    private static boolean hasGenerex(FormEntryPrompt questionPrompt) {

        List<TreeElement> bindAttributes = questionPrompt.getBindAttributes();

        return bindAttributes.stream().anyMatch(t -> t.getName().equals("generex"));
    }

    @SuppressWarnings("WeakerAccess")
    protected IAnswerProvider resolveProvider(FormEntryPrompt prompt) {
        IAnswerProvider iAnswerProvider = answerProviderMap.get(ControlDataTypeKey.fromPrompt(prompt));

        if (iAnswerProvider == null) {
            ControlDataTypeKey key = ControlDataTypeKey.with(Constants.CONTROL_INPUT, Constants.DATATYPE_TEXT);
            iAnswerProvider = answerProviderMap.get(key);
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

    public FormDef getFormDef() {
        return formDef;
    }

    public FormEntryModel getModel() {
        return model;
    }
}