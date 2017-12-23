package com.omnitech.javarosa.console;

import com.omnitech.javarosa.console.providers.MultiSelectProvider;
import com.omnitech.javarosa.console.providers.SelectOneProvider;
import com.omnitech.javarosa.console.providers.TextProvider;
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
    private Map<Integer, IAnswerProvider> answerProviderMap = new HashMap<>();

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

        //set up providers
        addProvider(Constants.CONTROL_SELECT_MULTI, MultiSelectProvider.class);
        addProvider(Constants.CONTROL_SELECT_ONE, SelectOneProvider.class);
        addProvider(Constants.CONTROL_TEXTAREA, TextProvider.class);


    }

    public <T extends IAnswerProvider> void addProvider(int controlType, Class<T> provider) {
        try {
            addProvider(controlType, provider.newInstance());
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    public void addProvider(int controlType, IAnswerProvider provider) {
        answerProviderMap.put(controlType, provider);
    }


    public void next() {
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
        return !hasEnded();
    }

    public boolean hasEnded() {
        return fec.getModel().getEvent() == FormEntryController.EVENT_END_OF_FORM;
    }

    private void handleQuestion() {

        FormEntryPrompt questionPrompt = model.getQuestionPrompt();

        LOG.fine(questionPrompt.getQuestionText());

        IAnswerProvider answerProvider = resolveProvider(questionPrompt);
        fec.answerQuestion(currentIndex(), answerProvider.acquire(fec, questionPrompt), true);

    }

    protected IAnswerProvider resolveProvider(FormEntryPrompt prompt) {
        IAnswerProvider iAnswerProvider = answerProviderMap.get(prompt.getControlType());
        if (iAnswerProvider == null) {
            iAnswerProvider = answerProviderMap.get(Constants.CONTROL_TEXTAREA);
        }
        return iAnswerProvider;
    }

    private FormIndex currentIndex() {
        return model.getFormIndex();
    }


    public ByteArrayPayload getSubmissionXml() throws IOException {
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

    public String getSubmissionXmlString() throws IOException {
        InputStream payloadStream = getSubmissionXml().getPayloadStream();
        return IOUtils.getText(payloadStream);
    }

}