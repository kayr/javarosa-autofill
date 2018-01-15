package com.github.kayr.javarosa.autofill.api.functions;

import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xpath.expr.XPathFuncExpr;

import java.util.ArrayList;
import java.util.List;

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
        String       filePath = XPathFuncExpr.toString(args[0]);
        List<String> lines    = IOUtils.loadFile(filePath);
        return Fakers.getRandom(lines);
    }


}
