package com.omnitech.javarosa.autofill.api;

import com.omnitech.javarosa.autofill.api.functions.*;
import com.omnitech.javarosa.autofill.api.providers.*;
import org.javarosa.core.io.Std;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormAutoFill {

    static {
        Std.setErr(new NullPrintStream());
        Std.setOut(new NullPrintStream());
    }

    private static Logger LOG = Logger.getLogger(FormAutoFill.class.getName());

    private Map<ControlDataTypeKey, IAnswerProvider> answerProviderMap = new HashMap<>();
    private Map<String, String>                      genExpressionMap  = new HashMap<>();
    private GenerexProvider                          generexProvider   = new GenerexProvider();

    private FormDef             formDef;
    private FormEntryModel      model;
    private FormEntryController fec;


    @SuppressWarnings("WeakerAccess")
    public FormAutoFill(FormDef formDef) {
        this.formDef = formDef;
        init();

    }

    public static FormAutoFill fromXml(String xForm) {
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

        registerAnswerProviders();

        registerFunctionHandlers();
    }

    private void registerAnswerProviders() {
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

        //Video Picture
        addProvider(Constants.CONTROL_VIDEO_CAPTURE, Constants.DATATYPE_BINARY, VideoProvider.class);
        addProvider(Constants.CONTROL_IMAGE_CHOOSE, Constants.DATATYPE_BINARY, ImageProvider.class);
        addProvider(Constants.CONTROL_AUDIO_CAPTURE, Constants.DATATYPE_BINARY, AudioProvider.class);

    }

    private void registerFunctionHandlers() {
        EvaluationContext ec = formDef.getEvaluationContext();

        Arrays.asList(
                new FnValue(),
                new FnSelectCell(),
                new FnRandomRegex(),
                new FnEvalAll(),
                new FnRandomSelectFromFile())
              .forEach(ec::addFunctionHandler);

        ListFunctions.registerAll(formDef);

        Fakers.registerAllHandlers(formDef);
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


    public FormAutoFill autoFill() {
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
                LOG.fine("Event: -> Form Begin");
                break;
            case FormEntryController.EVENT_END_OF_FORM:
                LOG.fine("Event: -> Form End");
                break;
            case FormEntryController.EVENT_GROUP:
                LOG.finer("----------Form Group");
                break;
            case FormEntryController.EVENT_PROMPT_NEW_REPEAT:
                LOG.finer("----------Form New Repeat");
                handleRepeat();
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

    private void handleRepeat() {
        IFormElement formElement = model.getCaptionPrompt().getFormElement();

        Optional<String> generex = getGenerex(formElement);

        boolean createNewRepeat = generex
                .map(gx -> {
                    IAnswerData data = generexProvider.acquire(formDef, formElement, currentIndex(), generex.get());
                    return Boolean.TRUE.equals(data.getValue());
                })
                .orElseGet(Fakers::randomBoolean);


        if (createNewRepeat) {
            LOG.fine("Adding new repeat");
            fec.newRepeat();
        }
    }


    @SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
    public boolean isEndOfForm() {
        return fec.getModel().getEvent() == FormEntryController.EVENT_END_OF_FORM;
    }

    private void handleQuestion() {

        FormEntryPrompt questionPrompt = model.getQuestionPrompt();


        Optional<String> generex = getGenerex(questionPrompt.getQuestion());

        IAnswerData answer = generex
                .map(gx -> generexProvider.acquire(formDef, questionPrompt.getQuestion(), currentIndex(), gx))
                .orElseGet(() -> resolveProvider(questionPrompt).acquire(fec, questionPrompt));


        int status = fec.answerQuestion(currentIndex(), answer, true);

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Answer for Question[" + questionPrompt.getQuestion().getBind().getReference() + "] = " + answer.getValue());
        }

        if (status != FormEntryController.ANSWER_OK)
            throw new IllegalArgumentException("Invalid Answer[" + answer.getValue() + "] For Question[" + questionPrompt.getQuestion().getBind().getReference() + "]");


    }

    private Optional<String> getGenerex(IFormElement formElement) {


        String hasGenerexMapping = genExpressionMap.get(FormUtils.resolveVariable(formElement));

        if (hasGenerexMapping != null) {
            return Optional.of(hasGenerexMapping);
        }

        FormIndex      index     = currentIndex();
        XPathReference reference = FormUtils.getSafeXpathReference(formElement, index.getReference());


        return FormUtils.getBindAttribute(formDef, reference, "generex");
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

    public FormAutoFill addGenerex(String id, String generex) {
        genExpressionMap.put(id, generex);
        return this;
    }

    public FormAutoFill addGenerex(Map<String, String> generexMap) {
        genExpressionMap.putAll(generexMap);
        return this;
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

    public String getSubmissionXml() {
        try (InputStream payloadStream = getSubmissionPayload().getPayloadStream()) {
            return IOUtils.getText(payloadStream);
        } catch (IOException e) {
            throw new AutoFillException(e);
        }
    }

    public FormDef getFormDef() {
        return formDef;
    }


}