package com.omnitech.javarosa.autofill.api.functions;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FnRandomSelectFromFile implements ISimpleFunctionHandler {

    public static final String AUTO_FILL_BASE_PATH = "autofill.base.path";

    @Override
    public List<Class[]> getPrototypes() {
        Class[]       classes    = {String.class};
        List<Class[]> prototypes = new ArrayList<>();
        prototypes.add(classes);
        return prototypes;
    }

    @Override
    public String getName() {
        return "random-select-from-file";
    }

    @Override
    public Object evalImpl(Object[] args, EvaluationContext ec) throws Exception {
        String basePathEnv  = System.getenv(AUTO_FILL_BASE_PATH);
        String basePathProp = System.getProperty(AUTO_FILL_BASE_PATH);
        String basePath     = basePathEnv != null ? basePathEnv : basePathProp;

        String       filePath = XPathFuncExpr.toString(args[0]);
        Path         path     = basePath != null ? Paths.get(basePath, filePath) : Paths.get(filePath);
        List<String> lines    = Files.lines(path).collect(Collectors.toList());


        return Fakers.getRandom(lines);
    }


}