package com.omnitech.javarosa.console.functions

import com.omnitech.javarosa.console.FormAutoFill
import org.javarosa.xpath.XPathTypeMismatchException

public class NumberTest extends GroovyTestCase {


    void testEval() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        def answer = new Fakers.Number().eval([1_000_000_000.0] as Object[], ec)
        assert answer > 1_000_000_000

        answer = new Fakers.Number().eval([2.0, 7.0] as Object[], ec)
        assert answer >= 2.0 && answer <= 7.0

        answer = new Fakers.Number().eval([2, 1, 2] as Object[], ec)
        assert answer >= 1.0 && answer <= 2.0

        answer = new Fakers.Number().eval([] as Object[], ec)
        assert answer
    }


    void testTypeMismatch() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        shouldFailWithCause(XPathTypeMismatchException) {
            new Fakers.Number().eval([1_000_000_000.0, ""] as Object[], ec)
        }

    }
}