package com.omnitech.javarosa.console.providers;

import com.omnitech.javarosa.console.FormUtils;
import com.omnitech.javarosa.console.IAnswerProvider;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.List;
import java.util.stream.Collectors;

public class MultiSelectProvider implements IAnswerProvider {


    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        List<SelectChoice> selectChoices = prompt.getSelectChoices();
        return selectMultiData(selectChoices);
    }

    private static SelectMultiData selectMultiData(List<SelectChoice> choices) {
        List<Selection> selections =
                FormUtils.getRandomMany(choices)
                         .stream()
                         .distinct()
                         .map(Selection::new)
                         .collect(Collectors.toList());
        return new SelectMultiData(selections);
    }
}
