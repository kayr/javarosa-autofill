package com.github.kayr.javarosa.autofill.api.functions;

import org.javarosa.core.model.FormDef;
import org.javarosa.xpath.XPathNodeset;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.kayr.javarosa.autofill.api.functions.ISimpleFunctionHandler.createFn;

public class FnGenelFunctions {

    public static void registerAll(FormDef formDef) {

        Arrays.asList(
                createFn("print")
                        .setHandler((objects, evaluationContext) -> {
                                        System.out.print("XPATH.print(): ");
                                        System.out.println(Arrays.stream(objects)
                                                                 .map(FnGenelFunctions::toString)
                                                                 .collect(Collectors.toList()));
                                        return "";
                                    }

                        )
        ).forEach(formDef.getEvaluationContext()::addFunctionHandler);


    }

    private static String toString(Object obj) {
        if (obj instanceof XPathNodeset) {
            XPathNodeset nodeset = (XPathNodeset) obj;

            switch (nodeset.size()) {
                case 0:
                case 1:
                    return XPathFuncExpr.toString(nodeset);
                default:
                    return "(" + nodeset.size() + ")" + IntStream.range(0, nodeset.size()).mapToObj(i -> toString(nodeset.getValAt(i))).reduce((s, s2) -> s + "/" + s2).orElse("");
            }

        }
        return XPathFuncExpr.toString(obj);
    }
}