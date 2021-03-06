package com.github.kayr.javarosa.autofill.api.functions;

import com.github.javafaker.Faker;
import com.github.kayr.javarosa.autofill.api.AutoFillException;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.utils.DateUtils;
import org.javarosa.xpath.XPathArityException;
import org.javarosa.xpath.XPathTypeMismatchException;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Fakers {

    public static final Faker faker = Faker.instance();

    public static void registerAllHandlers(FormDef formDef) {

        EvaluationContext ec = formDef.getEvaluationContext();

        Arrays.asList(new FnFake(),
                      new FnNumber(),
                      new FnDateBetween(),
                      new FnDateFuture(),
                      new FnBoolean(),
                      new FnDatePast(),
                      new FnDecimal()).forEach(ec::addFunctionHandler);
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

    public static boolean randomBoolean() {
        return faker.bool().bool();
    }

    public static <T> T getRandom(List<T> choices) {
        if (choices.size() < 3) {
            choices = _duplicate(choices, 2);
        }
        return faker.options().nextElement(choices);
    }

    private static <T> List<T> _duplicate(List<T> items, int number) {

        ArrayList<T> list = new ArrayList<>(items.size() * number);

        IntStream.rangeClosed(1, number).forEach(i -> list.addAll(items));

        return list;

    }

    public static <T> List<T> getRandomMany(List<T> choices) {
        List<T> temporaryChoices = _getRandomChoices(choices);
        while (temporaryChoices.isEmpty()) {
            temporaryChoices = _getRandomChoices(choices);
        }
        return temporaryChoices;

    }

    private static <T> List<T> _getRandomChoices(List<T> choices) {
        return choices.stream()
                      .filter(c -> randomBoolean())
                      .collect(Collectors.toList());
    }


    @SuppressWarnings("WeakerAccess")
    public static double round(BigDecimal num, int scale) {
        return num.setScale(scale, BigDecimal.ROUND_HALF_UP)
                  .doubleValue();
    }

    @SuppressWarnings("WeakerAccess")
    public static BigDecimal randomDecimal(long min, long max) {
        if (min == max) {
            return new BigDecimal(min);
        }

        long trueMin = Math.min(min, max);
        long trueMax = Math.max(min, max);
        if (trueMax <= 0) {
            trueMax = trueMax - 1;//fix..if both min and max are negative.. then max can become inclusive
        }
        return randomDecimal((double) trueMin, (double) trueMax);

    }

    public static BigDecimal randomDecimal(double min, double max) {
        if (min == max) {
            return new BigDecimal(min);
        }
        final double trueMin = Math.min(min, max);
        final double trueMax = Math.max(min, max);

        final double range = Math.abs(trueMax - trueMin);
        final double adj   = range * faker.random().nextDouble();

        return new BigDecimal(trueMin + adj);
    }

    public static long randomLong(long min, long max) {
        BigDecimal bigDecimal = randomDecimal(min, max);
        return bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).longValue();
    }


    static class FnFake implements ISimpleFunctionHandler {

        private static Object resolveChainCall(Object[] methodNames) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Deque<Object> stack = new LinkedList<>();
            stack.addAll(Arrays.asList(methodNames));
            Object context = faker;
            while (!stack.isEmpty()) {
                String methodName = XPathFuncExpr.toString(stack.pop());
                Method method     = context.getClass().getMethod(methodName);
                context = method.invoke(context);
            }

            return context;


        }

        @Override
        public String getName() {
            return "fake";
        }

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

            return resolveChainCall(args);

        }
    }

    public static class FnNumber implements ISimpleFunctionHandler {
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
            long max = 10;
            long min = 1;

            switch (args.length) {
                case 0:
                    break;
                case 1:
                    max = FunctionUtils._long(args, 0);
                    break;
                case 2:
                    min = FunctionUtils._long(args, 0);
                    max = FunctionUtils._long(args, 1);
                    break;
                default:
                    throw new XPathArityException(getName(), "<= 2", args.length);
            }
            return randomLong(min, max);
        }

    }

    public static class FnDecimal implements ISimpleFunctionHandler {
        @Override
        public String getName() {
            return "random-decimal";
        }


        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {
            boolean allNumbers = args.length == 0 || Arrays.stream(args).allMatch(a -> a instanceof java.lang.Number);

            if (!allNumbers) {
                throw new XPathTypeMismatchException(getName() + "() Only Supports Int Parameters");
            }

            double max   = 10;
            double min   = 1;
            int    round = 2;

            switch (args.length) {
                case 0:
                    break;
                case 1:
                    max = FunctionUtils._double(args, 0);
                    break;
                case 2:
                    min = FunctionUtils._double(args, 0);
                    max = FunctionUtils._double(args, 1);
                    break;
                case 3:
                    min = FunctionUtils._double(args, 0);
                    max = FunctionUtils._double(args, 1);
                    round = FunctionUtils._int(args, 2);
                    break;
                default:
                    throw new XPathArityException(getName(), "<= 3", args.length);
            }

            return round(randomDecimal(min, max), round);
        }


    }


    //random-date-between(from,to)
    public static class FnDateBetween implements ISimpleFunctionHandler {

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
    public static class FnDateFuture implements ISimpleFunctionHandler {

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
            int      atMost  = FunctionUtils._int(args, 0);
            TimeUnit unit    = toTimeUnit(XPathFuncExpr.toString(args[1]));

            return faker.date().future(atMost, unit, refDate);
        }


    }

    //random-past-date(atmost,unit,refdate)
    public static class FnDatePast implements ISimpleFunctionHandler {

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
            int      atMost  = FunctionUtils._int(args, 0);
            TimeUnit unit    = toTimeUnit(XPathFuncExpr.toString(args[1]));

            return faker.date().past(atMost, unit, refDate);
        }


    }

    public static class FnBoolean implements ISimpleFunctionHandler {

        @Override
        public Object evalImpl(Object[] args, EvaluationContext ec) {
            return faker.bool().bool();
        }

        @Override
        public String getName() {
            return "random-boolean";
        }
    }


}
