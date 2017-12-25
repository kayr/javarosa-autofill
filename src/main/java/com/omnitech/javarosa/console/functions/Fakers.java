package com.omnitech.javarosa.console.functions;

import com.github.javafaker.Faker;
import com.omnitech.javarosa.console.FormUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class Fakers {
    static Faker faker = FormUtils.faker;

    public static void registerAll(FormDef formDef) {
        formDef.getEvaluationContext().addFunctionHandler(new Fake());
    }

    static class Fake implements ISimpleFunctionHandler {

        @Override
        public String getName() {
            return "fake";
        }

        @Override
        public Object eval(Object[] args, EvaluationContext ec) {
            try {
                return resolveValue(args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private Object resolveValue(Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Deque<Object> stack = new LinkedList<>();
            stack.addAll(Arrays.asList(args));
            Object context = faker;
            while (!stack.isEmpty()) {
                String methodName = XPathFuncExpr.toString(stack.pop());
                Method method     = context.getClass().getMethod(methodName);
                //noinspection JavaReflectionInvocation
                context = method.invoke(context);
            }

            return context;


        }
    }


}
