package com.omnitech.javarosa.console.functions;

import com.omnitech.javarosa.console.FormUtils;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandomSelectFromFile implements ISimpleFunctionHandler {

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
        String       filePath = XPathFuncExpr.toString(args[0]);
        Path         path     = Paths.get(filePath);
        List<String> lines    = Files.lines(path).collect(Collectors.toList());
        return FormUtils.getRandom(lines);
    }


}
