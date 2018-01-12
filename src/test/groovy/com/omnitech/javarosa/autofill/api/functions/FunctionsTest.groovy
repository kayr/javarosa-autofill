package com.omnitech.javarosa.autofill.api.functions

import com.omnitech.javarosa.autofill.api.SmallFormEvaluator
import org.javarosa.xpath.XPathArityException
import org.javarosa.xpath.XPathTypeMismatchException
import org.junit.Test

import java.nio.file.NoSuchFileException

import static groovy.test.GroovyAssert.shouldFailWithCause

class FunctionsTest implements SmallFormEvaluator {

    @Test
    void testFunctions() {
        assert evalXpath("""select-cell('"one","two,","three"',2)""") == 'three'

        assert evalXpath("""select-cell('"one","two,","three"',1)""") == 'two,'

        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')

        assert evalXpath("random-regex('077[0-9]{10}')").toString().startsWith('077')

        shouldFailWithCause(XPathArityException) {
            evalXpath("random-regex()").toString().startsWith('077')
        }
    }

    @Test
    void testSettingAndRetrievingVariables() {
        assert evalXpath("val('var1',5)") == 5

        assert evalXpath("val('var1')") == 5

        assert evalXpath("selected-at(val('record','one two three'),1)") == 'two'

        assert evalXpath("val('record')") == 'one two three'

    }

    @Test
    void testRandomNumber() {

        def answer = evalXpath("random-number(1000000000)")
        assert answer > 1_000_000_000

        answer = evalXpath("random-number(2.0,7.0)")
        assert answer >= 2.0 && answer <= 7.0

        answer = evalXpath("random-number(2,1,2)")
        assert answer >= 1.0 && answer <= 2.0

        answer = evalXpath("random-number()")
        assert answer != null

        shouldFailWithCause(XPathTypeMismatchException) {
            evalXpath("random-number(1000,'')")
        }

    }

    @Test
    void testRandomFromFile() {

        assert evalXpath("random-select-from-file('src\\test\\resources\\data\\options.txt')")

        shouldFailWithCause(NoSuchFileException) { evalXpath("random-select-from-file('fake-file-path')") }


        setAutoFillBasePath("bad-base-path")
        shouldFailWithCause(NoSuchFileException) {
            evalXpath("random-select-from-file('src/test/resources/data/options.txt')")
        }
        resetBasePath()

        setAutoFillBasePath(".")
        assert evalXpath("random-select-from-file('src/test/resources/data/options.txt')")
        resetBasePath()
    }

    private static String setAutoFillBasePath(String bas) {
        return System.setProperty(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH, bas)
    }

    private static void resetBasePath() {
        System.properties.remove(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH)
    }

}
