package com.omnitech.javarosa.autofill.api.functions;

import com.github.javafaker.Faker;
import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.FormUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.utils.DateUtils;
import org.javarosa.xpath.XPathArityException;
import org.javarosa.xpath.XPathTypeMismatchException;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Fakers {
    static Faker faker = FormUtils.faker;


    public static void registerAllHandlers(FormDef formDef) {

        EvaluationContext ec = formDef.getEvaluationContext();

        Arrays.asList(new Fake(),
                      new Number(),
                      new DateBetween(),
                      new DateFuture(),
                      new DatePast()).forEach(ec::addFunctionHandler);
    }

    private static long _long(Object[] args, int i) {
        return ((java.lang.Number) args[i]).longValue();
    }

    private static int _int(Object[] args, int i) {
        return ((java.lang.Number) args[i]).intValue();
    }

    private static java.util.Date parseDate(Object object) {
        String s = XPathFuncExpr.toString(object).trim();

        if (object instanceof java.util.Date) {
            return (java.util.Date) object;
        }

        if (isDate(s)) {
            return DateUtils.parseDate(s);
        }

        if (isDateTime(s)) {
            return DateUtils.parseDateTime(s);
        }

        if (isTime(s)) {
            return DateUtils.parseTime(s);
        }

        throw new AutoFillException("Invalid Date Format: " + s);

    }

    private static boolean isDateTime(String s) {
        return s.contains(":") && s.contains("-");
    }

    private static boolean isTime(String s) {
        return s.contains(":") && !s.contains("-");
    }

    private static boolean isDate(String s) {
        return s.contains("-") && !s.contains(":");
    }

    private static TimeUnit toTimeUnit(String unitString) {
        String lowerCaseStr = unitString.toLowerCase();
        switch (lowerCaseStr) {
            case "second":
            case "seconds":
                return TimeUnit.SECONDS;
            case "minute":
            case "minutes":
                return TimeUnit.MINUTES;
            case "hour":
            case "hours":
                return TimeUnit.HOURS;
            case "day":
            case "days":
                return TimeUnit.DAYS;
            default:
                throw new AutoFillException("Time Unit [" + unitString + "] not supported");

        }
    }


    static class Fake implements ISimpleFunctionHandler {

        @Override
        public String getName() {
            return "fake";
        }

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {
            try {
                return resolveValue(args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new AutoFillException(e);
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
        public Object evalImpl(Object[] args, EvaluationContext ec) {
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
                default:
                    return faker.number().randomDouble(_int(args, 0), _long(args, 1), _long(args, 2));
            }
        }


    }


    //random-date-between(from,to)
    public static class DateBetween implements ISimpleFunctionHandler {

        @Override
        public String getName() {
            return "random-date-between";
        }

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {
            if (args.length != 2) {
                throw new XPathArityException(getName(), 2, args.length);
            }
            java.util.Date from = parseDate(args[0]);
            java.util.Date to   = parseDate(args[1]);
            return faker.date().between(from, to);
        }


    }

    //random-future-date(atmost,unit,refdate)
    public static class DateFuture implements ISimpleFunctionHandler {

        @Override
        public String getName() {
            return "random-future-date";
        }

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {

            if (args.length < 2) {
                throw new XPathArityException(getName(), ">=2", args.length);
            }

            Date     refDate = args.length == 2 ? new Date() : parseDate(args[2]);
            int      atMost  = _int(args, 0);
            TimeUnit unit    = toTimeUnit(XPathFuncExpr.toString(args[1]));

            return faker.date().future(atMost, unit, refDate);
        }


    }

    //random-past-date(atmost,unit,refdate)
    public static class DatePast implements ISimpleFunctionHandler {

        @Override
        public String getName() {
            return "random-past-date";
        }

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {

            if (args.length < 2) {
                throw new XPathArityException(getName(), ">=2", args.length);
            }

            Date     refDate = args.length == 2 ? new Date() : parseDate(args[2]);
            int      atMost  = _int(args, 0);
            TimeUnit unit    = toTimeUnit(XPathFuncExpr.toString(args[1]));

            return faker.date().past(atMost, unit, refDate);
        }


    }


}