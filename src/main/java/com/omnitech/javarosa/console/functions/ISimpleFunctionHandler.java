package com.omnitech.javarosa.console.functions;

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
}
