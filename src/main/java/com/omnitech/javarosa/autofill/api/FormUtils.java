package com.omnitech.javarosa.autofill.api;

import com.github.javafaker.Faker;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.UncastData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.xform.util.XFormUtils;
import org.javarosa.xpath.XPathParseTool;
import org.javarosa.xpath.expr.XPathExpression;
import org.javarosa.xpath.expr.XPathFuncExpr;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class FormUtils {

    public static final Faker faker = new Faker();

    private static final List<Boolean> booleans = duplicate(Arrays.asList(true, false), 5);


    public static String resolveVariable(IFormElement iFormElement) {
        Object        bind      = iFormElement.getBind().getReference();
        TreeReference reference = (TreeReference) bind;
        return reference.getNameLast();
    }

    public static FormDef parseFromText(String text) {
        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)))) {

            return XFormUtils.getFormRaw(reader);

        } catch (Exception e) {
            throw new AutoFillException("Error Parsing XML: ", e);
        }
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

    @SuppressWarnings("WeakerAccess")
    public static Optional<TreeElement> getTreeElement(FormDef formDef, IDataReference reference) {
        TreeElement treeElement = formDef.getMainInstance().resolveReference(reference);
        return Optional.ofNullable(treeElement);
    }


    public static UncastData evalXpathString(EvaluationContext ec, String xpath) throws XPathSyntaxException {
        XPathExpression xPathExpression = XPathParseTool.parseXPath(xpath);

        Object eval = xPathExpression.eval(ec);
        if (eval instanceof Number) {
            eval = ((Number) eval).doubleValue();//XPathFuncExpr.toString() Only loves doubles... so we convert all numbers to doubles
        }
        return new UncastData(XPathFuncExpr.toString(eval));
    }

    public static Optional<String> getBindAttribute(FormDef formDef, IDataReference element, String attribute) {
        Optional<TreeElement> treeElement1 = getTreeElement(formDef, element);
        return treeElement1.map(te -> te.getBindAttributeValue(null, attribute));

    }

    public static XPathReference getSafeXpathReference(IFormElement formElement, FormIndex index) {
        XPathReference reference = new XPathReference(index.getReference());

        if (formElement instanceof GroupDef) {
            GroupDef groupDef = (GroupDef) formElement;

            if (groupDef.getRepeat()) {

                //do not know a better way to find attributes for a repeat reliably.. for now
                //I will always use the first TreeReference on the mainInstance()
                XPathReference bind           = (XPathReference) formElement.getBind();
                TreeReference  treeReference  = (TreeReference) bind.getReference();
                TreeReference  cloneReference = treeReference.clone();

                IntStream.range(0, cloneReference.size())
                         .forEach(i -> cloneReference.setMultiplicity(i, 0));

                reference = new XPathReference(cloneReference);

            }
        }
        return reference;
    }
}