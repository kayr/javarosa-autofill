package com.omnitech.javarosa.console.functions;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.IFunctionHandler;

import java.util.Collections;
import java.util.List;

public interface ISimpleFunctionHandler extends IFunctionHandler {
    @Override
    default List<Class[]> getPrototypes() {
        return Collections.emptyList();
    }

    @Override
    default boolean rawArgs() {
        return true;
    }

    @Override
    default boolean realTime() {
        return false;
    }

    @Override
    default Object eval(Object[] args, EvaluationContext ec) {
        try {
            return evalImpl(args, ec);
        } catch (Throwable x) {
            throw new RuntimeException("Error evaluating function[" + getName() + "()]: " + x.getMessage(), x);
        }
    }

    Object evalImpl(Object[] args, EvaluationContext ec);
}
