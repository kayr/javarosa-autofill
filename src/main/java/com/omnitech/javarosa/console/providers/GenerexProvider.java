package com.omnitech.javarosa.console.providers;

import com.omnitech.javarosa.console.FormUtils;
import com.omnitech.javarosa.console.IAnswerProvider;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.UncastData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xpath.XPathParseTool;
import org.javarosa.xpath.expr.XPathExpression;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.util.Objects;

public class GenerexProvider implements IAnswerProvider {

    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        EvaluationContext ec = fec.getModel().getForm().getEvaluationContext();

        try {
            String          generex         = Objects.requireNonNull(FormUtils.getAttribute(prompt, "generex"));
            XPathExpression xPathExpression = XPathParseTool.parseXPath(generex);

            return new UncastData(xPathExpression.eval(ec).toString());
        } catch (XPathSyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
