package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormUtils;
import org.javarosa.core.model.*;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.Recalculate;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.xpath.XPathParseTool;
import org.javarosa.xpath.expr.XPathExpression;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.util.Objects;

public class GenerexProvider {

    public GenerexProvider() {
    }


    public IAnswerData acquire(FormDef fec, IFormElement element, FormIndex index, String generex) {

        EvaluationContext ec = fec.getEvaluationContext();

        try {

            Object value         = evalXpathString(ec, generex);
            int    finalDataType = element instanceof GroupDef ? Constants.DATATYPE_BOOLEAN : getDataType(fec, element, index);

            return Recalculate.wrapData(value, finalDataType);

        } catch (XPathSyntaxException e) {

            throw new AutoFillException("Error Evaluating Generex: " + e.getMessage() + " : For Question: " + element.getBind().getReference(), e);

        }

    }

    private int getDataType(FormDef fec, IFormElement prompt, FormIndex index) {

        TreeElement treeElement = fec.getMainInstance()
                                     .resolveReference(FormUtils.getSafeXpathReference(prompt, index.getReference()));


        Objects.requireNonNull(treeElement, "Could Not find Element For: " + index.getReference().toString());


        return treeElement.getDataType();
    }

    private static Object evalXpathString(EvaluationContext ec, String xpath) throws XPathSyntaxException {

        XPathExpression xPathExpression = XPathParseTool.parseXPath(xpath);

        Object eval = xPathExpression.eval(ec);

        if (eval instanceof Number) {
            eval = ((Number) eval).doubleValue();//XPathFuncExpr.toString() Only loves doubles... so we convert all numbers to doubles
        }

        return eval;
    }

}
