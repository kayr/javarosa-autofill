package com.omnitech.javarosa.console.providers;

import com.omnitech.javarosa.console.FormUtils;
import com.omnitech.javarosa.console.IAnswerProvider;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.List;

public class SelectOneProvider implements IAnswerProvider {


    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        List<SelectChoice> selectChoices = prompt.getSelectChoices();
        return selectOneData(selectChoices);
    }

    private static SelectOneData selectOneData(List<SelectChoice> choices) {
        SelectChoice selectChoice = FormUtils.getRandom(FormUtils.duplicate(choices, 2));
        return new SelectOneData(new Selection(selectChoice));
    }
}
