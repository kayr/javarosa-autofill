package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.IAnswerProvider;
import com.omnitech.javarosa.autofill.api.functions.Fakers;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.LongData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public class NumberProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        return new LongData(Fakers.faker.number().randomNumber());
    }
}
