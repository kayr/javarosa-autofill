package com.github.kayr.javarosa.autofill.api.functions

import com.github.kayr.javarosa.autofill.api.SmallFormEvaluator
import org.javarosa.xpath.XPathArityException
import org.javarosa.xpath.XPathTypeMismatchException
import org.javarosa.xpath.expr.XPathFuncExpr
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

        assert evalXpath("random-regex('[a-z]{1,10}\\@[a-z]{4,10}\\.[a-z]{3}')").toString().contains('@')

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


        def answer = evalXpath("random-number(-5,/f/number)")
        assert answer >= -5 && answer < XPathFuncExpr.unpack(evalXpath('/f/number'))

        answer = evalXpath("random-number(10)")
        assert answer <= 10

        answer = evalXpath("random-number(-10)")
        assert answer >= -10 && answer < 0

        answer = evalXpath("random-number(2.0,7.0)")
        assert answer >= 2.0 && answer < 7.0

        answer = evalXpath("random-number(2,1)")
        assert answer >= 1.0 && answer < 2.0

        answer = evalXpath("random-number(10,20)")
        assert answer >= 10.0 && answer < 20.0

        answer = evalXpath("random-number(20,10)")
        assert answer >= 10.0 && answer < 20.0

        answer = evalXpath("random-number(-5,10)")
        assert answer >= -5 && answer < 10

        answer = evalXpath("random-number(-5,-1)")
        assert answer >= -5 && answer < -1




        answer = evalXpath("random-number()")
        assert answer != null



        shouldFailWithCause(XPathTypeMismatchException) {
            evalXpath("random-number(1000,'')")
        }

    }

    @Test
    void testRandomNumberBounds() {
        1000.times {
            def start = -5
            def stop = -1
            def answer = Fakers.randomLong(start, stop)
            assert answer >= start && answer < stop
        }

        100.times {
            def start = -2
            def stop = 0
            def answer = Fakers.randomLong(start, stop)
            assert answer >= start && answer < stop
        }
        100.times {
            def start = -1
            def stop = -1
            def answer = Fakers.randomLong(start, stop)
            assert answer == start
        }

        1000.times {
            def start = -5
            def stop = 1
            def answer = Fakers.randomLong(start, stop)
            assert answer >= start && answer < stop
        }

        1000.times {
            def start = 5
            def stop = 10
            def answer = Fakers.randomLong(start, stop)
            assert answer >= start && answer < stop
        }
    }

    @Test
    void testRandomDecimal() {

        def answer = evalXpath("random-decimal(10)")
        assert answer < 10

        answer = evalXpath("random-decimal(2.0,7.0)")
        assert answer >= 2.0 && answer < 7.0

        answer = evalXpath("random-decimal(2,1,2)")
        assert answer >= 1.0 && answer < 2.0
        println(answer)

        answer = evalXpath("random-decimal(10,20,2)")
        assert answer >= 10.0 && answer < 20.0

        answer = evalXpath("random-decimal(10.1002,10.9,2)")
        assert answer >= 10.1 && answer < 10.9
        assert "$answer" ==~ "[0-9]+.[0-9]{0,2}"


        answer = evalXpath("random-decimal(-10.1002,-10.9,3)")
        assert answer < -10.1 && answer >= -10.9
        assert "$answer" ==~ "-[0-9]+.[0-9]{0,3}"

        answer = evalXpath("random-decimal()")
        assert answer != null
        println(answer)


        shouldFailWithCause(XPathTypeMismatchException) {
            evalXpath("random-decimal(1000,'')")
        }

    }

    @Test
    void testRandomFloatBounds() {
        100000.times {
            def start = -1d
            def stop = 6.1d
            def answer = Fakers.randomDecimal(start, stop)
            assert answer >= start && answer < stop
        }
    }


    @Test
    void testRandomFromFile() {

        assert evalXpath("random-select-from-file('src/test/resources/data/options.txt')")

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

    @Test
    void testEvalAll() {
        assert evalXpath('eval-all(2,3,8,9.8)') == 9.8
    }


    private static String setAutoFillBasePath(String bas) {
        return System.setProperty(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH, bas)
    }

    private static void resetBasePath() {
        System.properties.remove(FnRandomSelectFromFile.AUTO_FILL_BASE_PATH)
    }

}
