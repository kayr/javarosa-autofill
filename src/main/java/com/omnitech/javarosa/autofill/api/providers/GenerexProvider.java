package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormUtils;
import com.omnitech.javarosa.autofill.api.IAnswerProvider;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.UncastData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xpath.XPathParseTool;
import org.javarosa.xpath.expr.XPathExpression;
import org.javarosa.xpath.expr.XPathFuncExpr;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.util.Objects;

public class GenerexProvider implements IAnswerProvider {

    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        EvaluationContext ec = fec.getModel().getForm().getEvaluationContext();

        try {
            String          generex         = Objects.requireNonNull(FormUtils.getAttribute(prompt, "generex"));
            XPathExpression xPathExpression = XPathParseTool.parseXPath(generex);

            Object eval = xPathExpression.eval(ec);
            if (eval instanceof Number) {
                eval = ((Number) eval).doubleValue();//XPathFuncExpr.toString() Only loves doubles... so we convert all numbers to doubles
            }
            return new UncastData(XPathFuncExpr.toString(eval));
        } catch (XPathSyntaxException e) {
            throw new AutoFillException("Error Evaluating Generex: " + e.getMessage() + " : For Question: " + prompt.getQuestion().getBind().getReference(), e);
        }

    }
}
