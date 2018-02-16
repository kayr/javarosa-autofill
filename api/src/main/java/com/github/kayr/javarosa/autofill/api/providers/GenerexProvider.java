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
import java.util.logging.Logger;

public class GenerexProvider {

    private static Logger                       LOG             = Logger.getLogger(GenerexProvider.class.getName());
    private static Map<String, XPathExpression> xpathParseCache = new ConcurrentHashMap<>();


    public GenerexProvider() {
    }

    private static int getDataType(FormDef fec, IFormElement prompt, FormIndex index) {

        TreeElement treeElement = fec.getMainInstance()
                                     .resolveReference(FormUtils.getSafeXpathReference(prompt, index.getReference()));


        Objects.requireNonNull(treeElement, "Could Not find Element For: " + index.getReference().toString());


        return treeElement.getDataType();
    }

    @SuppressWarnings("RedundantThrows")
    private static Object evalXpathString(FormInstance instance, EvaluationContext baseContext, TreeReference reference, String xpath) throws XPathSyntaxException {

        XPathExpression   xPathExpression = parseXpath(xpath);
        EvaluationContext ec2             = new EvaluationContext(baseContext, reference);

        LOG.fine(String.format("Evaluating xpath [%s] in base[%s] context[%s]", xpath, baseContext.getContextRef(), reference));

        Object result = xPathExpression.eval(instance, ec2);

        ec2.isConstraint = true;

        if (result instanceof Number) {
            result = ((Number) result).doubleValue();//XPathFuncExpr.toString() Only loves doubles... so we convert all numbers to doubles
        }

        return result;
    }

    private static XPathExpression parseXpath(String xpath) {

        return xpathParseCache.computeIfAbsent(xpath, (xp) -> {
            try {
                return XPathParseTool.parseXPath(xp);
            } catch (XPathSyntaxException e) {
                return IOUtils.sneakyThrow(e);
            }
        });

    }

    public IAnswerData acquire(FormDef fec, IFormElement element, FormIndex index, String generex) {

        EvaluationContext ec = fec.getEvaluationContext();

        try {

            TreeReference reference     = element instanceof GroupDef ? resolveParentReference(fec, element, index) : index.getReference();
            Object        value         = evalXpathString(fec.getMainInstance(), ec, reference, generex);
            int           finalDataType = element instanceof GroupDef ? Constants.DATATYPE_BOOLEAN : getDataType(fec, element, index);


            return Recalculate.wrapData(XPathFuncExpr.unpack(value), finalDataType);

        } catch (XPathSyntaxException e) {

            throw new AutoFillException("Error Evaluating Generex: " + e.getMessage() + " : For Question: " + element.getBind().getReference(), e);

        } catch (Exception x) {
            throw new AutoFillException(String.format("Error Evaluating Generex For[ %s ]: Because %s", FormUtils.resolveVariable(element), x.getMessage()), x);
        }

    }

    /*
      It seems there is a bug or I cannot figure out how to get repeats to calculate on there context
      If you are in a repeat un cannot do a ../../ and be guaranteed to get the inner repeat.
      So to work around this we manually contextualize of xpath and add multiplicities based on the
      index. However if the repeat is outer we use the current repeat value as the context.
     */
    private TreeReference resolveParentReference(FormDef fec, IFormElement element, FormIndex index) {
        IFormElement parent = FormUtils.getParent(fec, element);
        if (parent instanceof FormDef) {
            parent = element;
            return FormUtils.getTreeReference(parent);
        } else {
            TreeReference treeReference = FormUtils.getTreeReference(parent);
            return contextualize(treeReference, index.getReference());
        }
    }

    private TreeReference contextualize(TreeReference reference, TreeReference context) {
        int           size  = reference.size();
        TreeReference clone = reference.clone();
        for (int i = 0; i < size; i++) {
            clone.setMultiplicity(i, context.getMultiplicity(i));
        }
        return clone;

    }

}
