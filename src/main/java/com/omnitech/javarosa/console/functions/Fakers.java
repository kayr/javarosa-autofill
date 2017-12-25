package com.omnitech.javarosa.console.functions;

import com.github.javafaker.Faker;
import com.omnitech.javarosa.console.FormUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathTypeMismatchException;
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
        formDef.getEvaluationContext().addFunctionHandler(new Number());
    }

    private static long _long(Object[] args, int i) {
        return ((java.lang.Number) args[i]).longValue();
    }

    private static int _int(Object[] args, int i) {
        return ((java.lang.Number) args[i]).intValue();
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


    public static class Number implements ISimpleFunctionHandler {
        @Override
        public String getName() {
            return "random-number";
        }


        @Override
        public Object eval(Object[] args, EvaluationContext ec) {
            boolean allNumbers = args.length == 0 || Arrays.stream(args).allMatch(a -> a instanceof java.lang.Number);

            if (!allNumbers) {
                throw new XPathTypeMismatchException(getName() + "() Only Supports Int Parameters");
            }

            switch (args.length) {
                case 0:
                    return faker.number().randomNumber();
                case 1:
                    return faker.number().numberBetween(_long(args, 0), Integer.MAX_VALUE);
                case 2:
                    return faker.number().numberBetween(_long(args, 0), _long(args, 1));
                case 3:
                    return faker.number().randomDouble(_int(args, 0), _long(args, 1), _long(args, 2));
            }
            return null;
        }


    }
}
