package com.omnitech.javarosa.console.providers;

import com.omnitech.javarosa.console.FormUtils;
import com.omnitech.javarosa.console.IAnswerProvider;
import org.javarosa.core.model.data.DateData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.Date;

public class DateProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        return new DateData(FormUtils.faker.date().between(new Date(0), new Date()));
    }
}
