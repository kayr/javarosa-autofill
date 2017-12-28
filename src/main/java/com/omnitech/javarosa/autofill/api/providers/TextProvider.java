package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.FormUtils;
import com.omnitech.javarosa.autofill.api.IAnswerProvider;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public class TextProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        return new StringData(FormUtils.faker.lorem().sentence());
    }
}
