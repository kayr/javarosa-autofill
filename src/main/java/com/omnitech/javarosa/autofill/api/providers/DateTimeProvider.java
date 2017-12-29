package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.FormUtils;
import com.omnitech.javarosa.autofill.api.IAnswerProvider;
import org.javarosa.core.model.data.DateTimeData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.Date;

public class DateTimeProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        return new DateTimeData(FormUtils.faker.date().between(new Date(0), new Date()));
    }
}