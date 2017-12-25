package com.omnitech.javarosa.console.functions

import com.omnitech.javarosa.console.FormAutoFill
import com.omnitech.javarosa.console.SmallFormEvaluator
import org.javarosa.xpath.XPathTypeMismatchException

public class FakerTests extends GroovyTestCase implements SmallFormEvaluator {


    void testEval() {
        println()
        assert !evalXpath("fake('name','firstName')").toString().isEmpty()
        shouldFail(RuntimeException) {
            evalXpath("fake('name','firstNames')")
        }
    }


    void testTypeMismatch() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        shouldFail(XPathTypeMismatchException) {
            new Fakers.Number().eval([1_000_000_000.0, ""] as Object[], ec)
        }

    }
}