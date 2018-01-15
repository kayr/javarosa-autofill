package com.github.kayr.javarosa.autofill.api


import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.xpath.XPathParseTool

trait SmallFormEvaluator {
    def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

    EvaluationContext ec = autoFiller.getFormDef().evaluationContext


    Object evalXpath(String xpath) {
        return XPathParseTool.parseXPath(xpath).eval(ec)
    }

}