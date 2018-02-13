package com.github.kayr.javarosa.autofill.api.providers;

import com.github.kayr.javarosa.autofill.api.functions.Fakers;
import org.javarosa.core.model.data.BooleanData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public class BooleanProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        return new BooleanData(Fakers.randomBoolean());
    }
}
