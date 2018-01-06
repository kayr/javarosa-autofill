package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.UncastData;
import org.javarosa.xpath.parser.XPathSyntaxException;

import java.util.Map;

public class GenerexProvider {

    private final Map<String, String> genExpressionMap;

    public GenerexProvider(Map<String, String> genExpressionMap) {
        this.genExpressionMap = genExpressionMap;
    }

    public UncastData acquire(FormDef fec, IFormElement prompt, String generex) {
        EvaluationContext ec = fec.getEvaluationContext();

        try {

            return FormUtils.evalXpathString(ec, generex);
        } catch (XPathSyntaxException e) {
            throw new AutoFillException("Error Evaluating Generex: " + e.getMessage() + " : For Question: " + prompt.getBind().getReference(), e);
        }

    }

}
