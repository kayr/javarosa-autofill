package com.omnitech.javarosa.console.functions;

import com.github.javafaker.Faker;
import com.omnitech.javarosa.console.FormUtils;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathTypeMismatchException;

import java.util.Arrays;

public class RandomNumber implements ISimpleFunctionHandler {
    @Override
    public String getName() {
        return "random-number";
    }

    private Faker faker = FormUtils.faker;

    @Override
    public Object eval(Object[] args, EvaluationContext ec) {
        boolean allNumbers = args.length == 0 || Arrays.stream(args).allMatch(a -> a instanceof Number);

        if (!allNumbers) {
            throw new XPathTypeMismatchException(getName() + "() Only Supports Int Parameters");
        }

        return returnRandomNumber(args);
    }

    private Object returnRandomNumber(Object[] args) {
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

    private long _long(Object[] args, int i) {
        return ((Number) args[i]).longValue();
    }

    private int _int(Object[] args, int i) {
        return ((Number) args[i]).intValue();
    }
}
