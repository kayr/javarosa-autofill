package com.omnitech.javarosa.console;

import com.github.javafaker.Faker;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.xform.util.XFormUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class FormUtils {

    public static Faker faker = new Faker();

    static FormDef parseFromText(String text) throws IOException {
        InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
        return XFormUtils.getFormRaw(reader);
    }

    public static SelectOneData selectOneData(List<SelectChoice> choices) {
        SelectChoice selectChoice = getRandom(_duplicate(choices, 5));
        return new SelectOneData(new Selection(selectChoice));
    }

    public static SelectMultiData selectMultiData(List<SelectChoice> choices) {
        List<Selection> selections =
                getRandomMany(_duplicate(choices, 5))
                        .stream()
                        .distinct()
                        .map(Selection::new)
                        .collect(Collectors.toList());
        return new SelectMultiData(selections);
    }

    public static boolean randomBoolean() {
        return getRandom(booleans);
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> T getRandom(List<T> choices) {
        int randomIndex = faker.number().numberBetween(0, choices.size() - 1);
        return choices.get(randomIndex);
    }


    private static List<Boolean> booleans = _duplicate(Arrays.asList(true, true, false, false), 8);

    @SuppressWarnings("WeakerAccess")
    public static <T> List<T> getRandomMany(List<T> choices) {
        List<T> temporaryChoices = _getRandomChoices(choices);
        while (temporaryChoices.isEmpty()) {
            temporaryChoices = _getRandomChoices(choices);
        }
        return temporaryChoices;

    }

    private static <T> List<T> _getRandomChoices(List<T> choices) {
        return choices.stream()
                      .filter(c -> getRandom(booleans))
                      .collect(Collectors.toList());
    }

    private static <T> List<T> _duplicate(List<T> items, int times) {
        List<T> dupes = new ArrayList<>(items.size() * times);
        IntStream.rangeClosed(0, times).forEach(i -> dupes.addAll(items));
        return dupes;
    }
}