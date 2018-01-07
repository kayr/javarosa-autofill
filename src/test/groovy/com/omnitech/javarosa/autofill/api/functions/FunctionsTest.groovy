package com.omnitech.javarosa.autofill.api.functions

import com.omnitech.javarosa.autofill.api.SmallFormEvaluator
import org.javarosa.xpath.XPathArityException
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFailWithCause

class FunctionsTest implements SmallFormEvaluator {

    @Test
    void testFunctions(){

        assert evalXpath("""select-cell('"one","two,","three"',2)""") == 'three'
        assert evalXpath("""select-cell('"one","two,","three"',1)""") == 'two,'


        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')
        shouldFailWithCause(XPathArityException) {
            evalXpath("random-regex()").toString().startsWith('077')
        }
    }

    @Test
    void testSettingAndRetrieving() {
        assert evalXpath("val('var1',5)") == 5
        assert evalXpath("val('var1')") == 5

        assert evalXpath("selected-at(val('record','one two three'),1)") == 'two'
        assert evalXpath("val('record')") == 'one two three'

    }
}
