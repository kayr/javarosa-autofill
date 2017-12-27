package com.omnitech.javarosa.console.functions;

import com.mifmif.common.regex.Generex;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathArityException;
import org.javarosa.xpath.expr.XPathFuncExpr;

public class RandomRegex implements ISimpleFunctionHandler {
    @Override
    public String getName() {
        return "random-regex";
    }

    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec) {
        if (args.length != 1) {
            throw new XPathArityException(getName(), "1 argument ", args.length);
        }
        Generex generex = new Generex(XPathFuncExpr.toString(args[0]));
        return generex.random();
    }
}