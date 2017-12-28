package com.omnitech.javarosa.console.functions

import com.omnitech.javarosa.console.SmallFormEvaluator
import org.javarosa.xpath.XPathArityException
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFailWithCause

class RandomRegexTest implements SmallFormEvaluator {

    @Test
    void eval() {
        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        shouldFailWithCause(XPathArityException) {
            evalXpath("random-regex()").toString().startsWith('077')
        }

    }


}