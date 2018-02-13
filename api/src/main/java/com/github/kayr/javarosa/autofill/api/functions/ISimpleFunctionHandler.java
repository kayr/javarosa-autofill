package com.github.kayr.javarosa.autofill.api.functions;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.IFunctionHandler;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

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
            throw new AutoFillException("Error evaluating function[" + getName() + "()]: " + x.getClass().getSimpleName() + ":" + x.getMessage(), x);
        }
    }

    Object evalImpl(Object[] args, EvaluationContext ec) throws Exception;

    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public static GenericHandler createFn(String name) {
        return new GenericHandler().setName(name);
    }

    class GenericHandler implements ISimpleFunctionHandler {

        String                                          name;
        BiFunction<Object[], EvaluationContext, Object> handler;


        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {
            return handler.apply(args, ec);
        }

        @Override
        public String getName() {
            return name;
        }

        public GenericHandler setName(String name) {
            this.name = name;
            return this;
        }

        public GenericHandler setHandler(BiFunction<Object[], EvaluationContext, Object> handler) {
            this.handler = handler;
            return this;
        }

        @Override
        public String toString() {
            return name + "()";
        }
    }
}
