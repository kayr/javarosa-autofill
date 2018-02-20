package com.github.kayr.javarosa.autofill.api.providers;

import com.github.kayr.javarosa.autofill.api.functions.Fakers;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.List;

public class SelectOneProvider implements IAnswerProvider {


    private static SelectOneData selectOneData(List<SelectChoice> choices) {
        if (choices.size() == 0) {
            return null;
        }
        SelectChoice selectChoice = Fakers.getRandom(choices);
        return new SelectOneData(new Selection(selectChoice));
    }

    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        List<SelectChoice> selectChoices = prompt.getSelectChoices();
        return selectOneData(selectChoices);
    }
}
