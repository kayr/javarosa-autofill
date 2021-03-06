package com.github.kayr.javarosa.autofill.api.providers;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public interface IAnswerProvider {


    IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt);
}
