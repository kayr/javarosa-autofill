package com.github.kayr.javarosa.autofill.api.functions;

import org.javarosa.xpath.XPathTypeMismatchException;

import java.util.List;

public class FunctionUtils {

    @SuppressWarnings({"WeakerAccess", "unchecked"})
    static <T> T _arg(Object[] args, Class<T> klass, int i) {
        Object arg = args[i];

        if (!klass.isInstance(arg)) {
            throw new XPathTypeMismatchException(String.format("Cannot Cast Item %s of Type %s To %s", arg, arg.getClass(), klass));
        }
        return ((T) arg);
    }

    @SuppressWarnings("unchecked")
    static List<Object> _list(Object[] args, int i) {
        return (List<Object>) _arg(args, List.class, i);
    }

    static long _long(Object[] args, int i) {
        return ((Number) args[i]).longValue();
    }

    static int _int(Object[] args, int i) {
        return ((Number) args[i]).intValue();
    }

    static String _string(Object[] args, int i) {
        return _arg(args, String.class, i);
    }
}
