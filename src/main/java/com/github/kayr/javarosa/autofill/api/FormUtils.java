package com.github.kayr.javarosa.autofill.api;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.GroupDef;
import org.javarosa.core.model.IDataReference;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.xform.util.XFormUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


public class FormUtils {

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


    @SuppressWarnings("WeakerAccess")
    public static Optional<TreeElement> getTreeElement(FormDef formDef, IDataReference reference) {
        TreeElement treeElement = formDef.getMainInstance().resolveReference(reference);
        return Optional.ofNullable(treeElement);
    }


    public static Optional<String> getBindAttribute(FormDef formDef, IDataReference element, String attribute) {
        Optional<TreeElement> treeElement1 = getTreeElement(formDef, element);
        return treeElement1.map(te -> te.getBindAttributeValue(null, attribute));

    }

    public static XPathReference getSafeXpathReference(IFormElement formElement, TreeReference pTreeRefrence) {
        XPathReference reference = new XPathReference(pTreeRefrence);

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

    public static List<IFormElement> getChildren(IFormElement element) {
        List<IFormElement> elements = new ArrayList<>();
        element.getChildren().forEach(c -> {
            if (!(c instanceof FormDef)) elements.add(c);
            List<IFormElement> children = c.getChildren();
            if (children != null && !children.isEmpty()) {
                elements.addAll(getChildren(c));
            }
        });

        return elements;
    }
}