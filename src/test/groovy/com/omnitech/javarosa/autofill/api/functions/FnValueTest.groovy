package com.omnitech.javarosa.autofill.api.functions

import com.omnitech.javarosa.autofill.api.SmallFormEvaluator
import org.junit.Test

class FnValueTest implements SmallFormEvaluator {

    @Test
    void testSettingAndRetrieving() {
        evalXpath("val('var1',5)") == 5
        evalXpath("val('var1')") == 5
    }


}