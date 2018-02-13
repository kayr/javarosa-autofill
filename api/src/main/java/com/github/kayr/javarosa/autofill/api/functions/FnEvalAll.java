package com.github.kayr.javarosa.autofill.api.functions;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathArityException;

public class FnEvalAll implements ISimpleFunctionHandler {
    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec) {
        if (args.length == 0) throw new XPathArityException(getName(), " > 0", 0);
        return args[args.length - 1];
    }

    @Override
    public String getName() {
        return "eval-all";
    }
}
