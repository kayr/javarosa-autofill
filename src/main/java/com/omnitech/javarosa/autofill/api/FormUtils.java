package com.omnitech.javarosa.autofill.api;

import com.github.javafaker.Faker;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xform.util.XFormUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class FormUtils {

    public static Faker faker = new Faker();

    private static List<Boolean> booleans = duplicate(Arrays.asList(true, false), 5);


    public static String getAttribute(FormEntryPrompt qn, String name) {
        Optional<TreeElement> generex = qn.getBindAttributes().stream().filter(x -> x.getName().equals(name)).findFirst();
        if (generex.isPresent()) {
            return generex.get().getValue().getValue().toString();
        } else {
            return null;
        }
    }

    static FormDef parseFromText(String text) throws IOException {
        InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
        return XFormUtils.getFormRaw(reader);
    }

    public static boolean randomBoolean() {
        return getRandom(booleans);
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> T getRandom(List<T> choices) {
        int randomIndex = faker.number().numberBetween(0, choices.size() - 1);
        return choices.get(randomIndex);
    }


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
                      .filter(c -> randomBoolean())
                      .collect(Collectors.toList());
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> List<T> duplicate(List<T> items, int times) {
        List<T> dupes = new ArrayList<>(items.size() * times);
        IntStream.rangeClosed(0, times).forEach(i -> dupes.addAll(items));
        return dupes;
    }
}