package com.github.kayr.javarosa.autofill.api.functions;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathArityException;
import org.javarosa.xpath.expr.XPathFuncExpr;

public class FnValue implements ISimpleFunctionHandler {
    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec)  {

        if (args.length == 1) return ec.getVariable(XPathFuncExpr.toString(args[0]));

        if (args.length != 2) throw new XPathArityException(getName(), 2, args.length);

        ec.setVariable(XPathFuncExpr.toString(args[0]), args[1]);
        return args[1];
    }

    @Override
    public String getName() {
        return "val";
    }
}
