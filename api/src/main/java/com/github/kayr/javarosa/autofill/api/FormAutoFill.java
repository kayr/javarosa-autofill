package com.github.kayr.javarosa.autofill.api;

import com.github.kayr.javarosa.autofill.api.functions.*;
import com.github.kayr.javarosa.autofill.api.providers.*;
import org.apache.commons.lang3.StringUtils;
import org.javarosa.core.io.Std;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.services.storage.StorageManager;
import org.javarosa.core.services.storage.util.DummyIndexedStorageUtility;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.core.services.transport.payload.IDataPayload;
import org.javarosa.core.services.transport.payload.MultiMessagePayload;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormAutoFill {

    private static Logger LOG = Logger.getLogger(FormAutoFill.class.getName());

    static {
        Std.setErr(new NullPrintStream());
        Std.setOut(new NullPrintStream());
    }

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

    public static String payloadToXml(IDataPayload submissionPayload) {
        try {

            IDataPayload finalPayLoad = submissionPayload;
            if (submissionPayload instanceof MultiMessagePayload) {
                MultiMessagePayload    multiPayload = (MultiMessagePayload) submissionPayload;
                Optional<IDataPayload> payload      = multiPayload.getPayloads().stream().filter(ByteArrayPayload.class::isInstance).findAny();

                finalPayLoad = payload.orElseThrow(() -> new AutoFillException("No XML Payload Found"));

            }
            InputStream payloadStream = finalPayLoad.getPayloadStream();
            return IOUtils.getText(payloadStream);
        } catch (IOException e) {
            throw new AutoFillException(e);
        }
    }

    private void init() {
        registerPreLoaders();

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

        FnGenelFunctions.registerAll(formDef);

    }

    private void registerPreLoaders() {
        StorageManager.setStorageFactory((name, type) -> new DummyIndexedStorageUtility());
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

    HashMap<IFormElement, Integer> repeatCounts = new HashMap<>();

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

    private long maxSeconds = TimeUnit.SECONDS.toMillis(10);
    private long startTime;

    public FormAutoFill autoFill() {
        startTime = System.currentTimeMillis();
        while (!isEndOfForm()) {
            isTimeOut();
            nextEvent();
        }

        formDef.postProcessInstance();

        ValidateOutcome validate = formDef.validate(true);

        if (validate != null) {
            FormEntryPrompt questionPrompt = fec.getModel().getQuestionPrompt(validate.failedPrompt);
            IAnswerData     answer         = questionPrompt.getAnswerValue();
            throw new IllegalArgumentException("Invalid Answer[" + answer.getValue() + "] For Question[" + questionPrompt.getQuestion().getLabelInnerText() + "]");

        }

        return this;
    }

    @SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
    public boolean isEndOfForm() {
        return fec.getModel().getEvent() == FormEntryController.EVENT_END_OF_FORM;
    }

    public void isTimeOut() {
        long currentTimeMillis = System.currentTimeMillis();

        if ((currentTimeMillis - startTime) >= maxSeconds) {
            throw new AutoFillException("Generation for form [" + formDef.getName() + "] timed out. This is usually due to long loops in repeats. Try adding binding constraints on repeats");
        }
    }
    private void handleRepeat() {
        IFormElement formElement = model.getCaptionPrompt().getFormElement();

        try {
            Optional<String> generex = getGenerex(formElement);

            boolean createNewRepeat = generex
                    .map(gx -> {
                        IAnswerData data = generexProvider.acquire(formDef, formElement, currentIndex(), generex.get());
                        return Boolean.TRUE.equals(data.getValue());
                    })
                    .orElseGet(Fakers::randomBoolean);


            if (createNewRepeat) {
                Integer count = repeatCounts.compute(formElement, (element, integer) -> integer == null ? 1 : integer + 1);
                if (count >= 100) {
                    throw new AutoFillException("Repeat Iterations Have Exceeded 100. Try To Add A Boundary Expression");
                }
                LOG.fine("Adding new repeat");
                fec.newRepeat();
            }
        }
        catch (Exception x) {
            throw new AutoFillException("Error Auto-Filling Repeat[" + FormUtils.resolveVariable(formElement) + "] " + x.getMessage(), x);
        }
    }

    private void handleQuestion() {

        FormEntryPrompt questionPrompt = model.getQuestionPrompt();
        QuestionDef     questionDef    = questionPrompt.getQuestion();


        try {
            Optional<String> generex = getGenerex(questionDef);

            IAnswerData answer = generex
                    .map(gx -> generexProvider.acquire(formDef, questionDef, currentIndex(), gx))
                    .orElseGet(() -> resolveProvider(questionPrompt).acquire(fec, questionPrompt));


            int status = fec.answerQuestion(currentIndex(), answer, true);

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Answer for Question[" + questionDef.getBind().getReference() + "] = " + FormUtils.safeGetAnswerText(answer));
            }

            if (status != FormEntryController.ANSWER_OK)
                throw new IllegalArgumentException("Invalid Answer[" + FormUtils.safeGetAnswerText(answer) + "] For Question[" + FormUtils.resolveVariable(questionPrompt.getFormElement()) + "]");
        }
        catch (Exception x) {
            throw new AutoFillException("Error Auto-Filling Question[" + FormUtils.resolveVariable(questionPrompt.getFormElement()) + "] " + x.getMessage(), x);
        }

    }

    private Optional<String> getGenerex(IFormElement formElement) {


        String generexMapping = genExpressionMap.get(FormUtils.resolveVariable(formElement));

        if (StringUtils.isNotBlank(generexMapping)) {
            return Optional.of(generexMapping);
        }

        FormIndex      index     = currentIndex();
        XPathReference reference = FormUtils.getSafeXpathReference(formElement, index.getReference());


        Optional<String> generex = FormUtils.getBindAttribute(formDef, reference, "generex");

        return generex.filter(StringUtils::isNotBlank);
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
    public IDataPayload getSubmissionPayload() throws IOException {
        FormInstance            instance   = getInstance();
        XFormSerializingVisitor serializer = new XFormSerializingVisitor();
        return serializer.createSerializedPayload(instance, getSubmissionDataReference());
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
        try {
            return payloadToXml(getSubmissionPayload());
        } catch (IOException e) {
            throw new AutoFillException(e);
        }

    }

    public FormDef getFormDef() {
        return formDef;
    }


}