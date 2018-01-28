package com.github.kayr.javarosa.autofill.api.providers;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.FormUtils;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.Recalculate;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.xpath.XPathParseTool;
import org.javarosa.xpath.expr.XPathExpression;
import org.javarosa.xpath.expr.XPathFuncExpr;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GenerexProvider {

    public GenerexProvider() {
    }


    public IAnswerData acquire(FormDef fec, IFormElement element, FormIndex index, String generex) {

        EvaluationContext ec = fec.getEvaluationContext();

        try {

            //if this is a group do not use the group reference.. so that we keep the context at the form level

            //workin
            //TreeReference reference     = element instanceof GroupDef ? FormUtils.getTreeReference(element) : index.getReference();

            //not woring
            TreeReference reference = element instanceof GroupDef ? resolveParentReference(fec, element, index) : index.getReference();
//            TreeReference reference     = index.getReference();
            Object value         = evalXpathString(fec.getMainInstance(), ec, reference, generex);
            int    finalDataType = element instanceof GroupDef ? Constants.DATATYPE_BOOLEAN : getDataType(fec, element, index);


            return Recalculate.wrapData(XPathFuncExpr.unpack(value), finalDataType);

        } catch (XPathSyntaxException e) {

            throw new AutoFillException("Error Evaluating Generex: " + e.getMessage() + " : For Question: " + element.getBind().getReference(), e);

        }

    }

    private TreeReference resolveParentReference(FormDef fec, IFormElement element, FormIndex index) {
        IFormElement parent = FormUtils.getParent(fec, element);
        if (parent instanceof FormDef) {
            parent = element;
            return FormUtils.getTreeReference(parent);
        } else {
            TreeReference treeReference = FormUtils.getTreeReference(parent);
//            return index.getReference().contextualize(treeReference);
            return contextualize(treeReference,index.getReference());
        }
//        return treeReference;
    }

    private TreeReference contextualize(TreeReference reference, TreeReference context) {
        int           size  = reference.size();
        TreeReference clone = reference.clone();
        for (int i = 0; i < size; i++) {
            clone.setMultiplicity(i, context.getMultiplicity(i));
        }
        return clone;

    }

    private static int getDataType(FormDef fec, IFormElement prompt, FormIndex index) {

        TreeElement treeElement = fec.getMainInstance()
                                     .resolveReference(FormUtils.getSafeXpathReference(prompt, index.getReference()));


        Objects.requireNonNull(treeElement, "Could Not find Element For: " + index.getReference().toString());


        return treeElement.getDataType();
    }

    private static Object evalXpathString(FormInstance instance, EvaluationContext baseContext, TreeReference reference, String xpath) throws XPathSyntaxException {

        XPathExpression   xPathExpression = parseXpath(xpath);
        EvaluationContext ec2             = new EvaluationContext(baseContext, reference);

        System.out.println(String.format("Evaluating xpath [%s] in base[%s] context[%s]", xpath, baseContext.getContextRef(), reference));

        Object result = xPathExpression.eval(instance, ec2);

        ec2.isConstraint = true;

        if (result instanceof Number) {
            result = ((Number) result).doubleValue();//XPathFuncExpr.toString() Only loves doubles... so we convert all numbers to doubles
        }

        return result;
    }

    private static Map<String, XPathExpression> xpathParseCache = new ConcurrentHashMap<>();

    private static XPathExpression parseXpath(String xpath) {

        return xpathParseCache.computeIfAbsent(xpath, (xp) -> {
            try {
                return XPathParseTool.parseXPath(xp);
            } catch (XPathSyntaxException e) {
                return IOUtils.sneakyThrow(e);
            }
        });

    }

}
