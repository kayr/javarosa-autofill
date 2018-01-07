package com.omnitech.javarosa.autofill.api.functions;

import org.javarosa.core.model.condition.EvaluationContext;

import java.util.Arrays;

public class FnEvalAll implements ISimpleFunctionHandler {
    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec)  {
        return Arrays.stream(args).findFirst();
    }

    @Override
    public String getName() {
        return "eval-all";
    }
}
