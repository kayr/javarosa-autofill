package com.omnitech.javarosa.autofill.api.functions

import com.omnitech.javarosa.autofill.api.FormAutoFill
import org.javarosa.xpath.XPathTypeMismatchException

class FnNumberTest extends GroovyTestCase {


    void testEval() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        def answer = new Fakers.FnNumber().eval([1_000_000_000.0] as Object[], ec)
        assert answer > 1_000_000_000

        answer = new Fakers.FnNumber().eval([2.0, 7.0] as Object[], ec)
        assert answer >= 2.0 && answer <= 7.0

        answer = new Fakers.FnNumber().eval([2, 1, 2] as Object[], ec)
        assert answer >= 1.0 && answer <= 2.0

        answer = new Fakers.FnNumber().eval([] as Object[], ec)
        assert answer != null
    }


    void testTypeMismatch() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        shouldFailWithCause(XPathTypeMismatchException) {
            new Fakers.FnNumber().eval([1_000_000_000.0, ""] as Object[], ec)
        }

    }
}