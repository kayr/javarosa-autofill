package com.omnitech.javarosa.console.functions

import com.omnitech.javarosa.console.SmallFormEvaluator
import org.javarosa.xpath.XPathArityException
import org.junit.Test

import static groovy.util.GroovyAssert.shouldFail
import static groovy.util.GroovyAssert.shouldFailWithCause

public class RandomRegexTest implements SmallFormEvaluator {

    @Test
    public void eval() {
        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        shouldFail(XPathArityException) {
            evalXpath("random-regex()").toString().startsWith('077')
        }

    }


}