package com.github.kayr.javarosa.autofill.api.functions;

import org.apache.commons.lang3.text.StrTokenizer;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.XPathArityException;
import org.javarosa.xpath.expr.XPathFuncExpr;

public class FnSelectCell implements ISimpleFunctionHandler {


    @Override
    public String getName() {
        return "select-cell";
    }

    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec) {

        if (args.length != 2) {
            throw new XPathArityException(getName(), 2, args.length);
        }

        StrTokenizer csvInstance = StrTokenizer.getCSVInstance();
        csvInstance.reset(XPathFuncExpr.toString(args[0]));

        String[] tokenArray = csvInstance.getTokenArray();

        return tokenArray[FunctionUtils._int(args, 1)];
    }


}
