package com.omnitech.javarosa.autofill.api.functions

import com.omnitech.javarosa.autofill.api.FormAutoFill
import com.omnitech.javarosa.autofill.api.SmallFormEvaluator
import groovy.time.TimeCategory
import org.javarosa.xpath.XPathTypeMismatchException

import static org.javarosa.core.model.utils.DateUtils.getDate

class FakerTests extends GroovyTestCase implements SmallFormEvaluator {


    void testEval() {
        assert !evalXpath("fake('name','firstName')").toString().isEmpty()
        println evalXpath("fake('bool')")
        shouldFail(RuntimeException) {
            evalXpath("fake('name','firstNames')")
        }
    }


    void testTypeMismatch() {

        def autoFiller = FormAutoFill.fromResource("/SmallForm.xml")

        def ec = autoFiller.getFormDef().evaluationContext

        shouldFailWithCause(XPathTypeMismatchException) {
            new Fakers.Number().eval([1_000_000_000.0, ""] as Object[], ec)
        }

    }



    void testDateBetween() {

        def now = new Date()

        assertBetween(
                evalXpath("random-date-between('2012-01-01','2013-01-01')"),
                getDate(2012, 1, 1),
                getDate(2013, 1, 1))

        assertBetween(
                evalXpath("random-date-between('1990-01-10','1990-01-13')"),
                getDate(1990, 1, 10),
                getDate(1990, 1, 13))

        assertBetween(
                evalXpath("random-date-between(now(),'2090-01-01')"),
                now,
                getDate(2090, 1, 1)
        )

        assertNotBetween(
                evalXpath("random-date-between(now(),'2090-01-01')"),
                now - 1000,
                now
        )


    }

    void testFutureDate() {

        def now = new Date()

        assertBetween(
                evalXpath("random-future-date(5,'days')"),
                now,
                now + 5)

        assertNotBetween(
                evalXpath("random-future-date(5,'days')"),
                now - 10,
                now)

        assertBetween(
                evalXpath("random-future-date(5,'days','2020-1-1')"),
                getDate(2020, 1, 1),
                getDate(2020, 1, 1) + 5)


        use(TimeCategory) {
            assertBetween(
                    evalXpath("random-future-date(5,'seconds','12:00')"),
                    getDate(1970, 1, 1) + 12.hours,
                    new Date(0) + 12.hours + 5.seconds)

            assertBetween(
                    evalXpath("random-future-date(5,'seconds','1980-1-1T12:00')"),
                    getDate(1980, 1, 1) + 12.hours,
                    getDate(1980, 1, 1) + 12.hours + 5.seconds)

            assertBetween(
                    evalXpath("random-future-date(5,'minutes')"),
                    now,
                    now + 5.minutes)

            assertBetween(
                    evalXpath("random-future-date(5,'minute')"),
                    now,
                    now + 5.minutes)

            assertBetween(
                    evalXpath("random-future-date(5,'second')"),
                    now,
                    now + 5.seconds)

            assertBetween(
                    evalXpath("random-future-date(5,'seconds')"),
                    now,
                    now + 5.seconds)

            assertBetween(
                    evalXpath("random-future-date(5,'hour')"),
                    now,
                    now + 5.hours)

            assertBetween(
                    evalXpath("random-future-date(5,'hours')"),
                    now,
                    now + 5.hours)


        }


    }

    void testPastDate() {

        def now = new Date()

        assertBetween(
                evalXpath("random-past-date(5,'days')"),
                now - 5,
                now)


        assertBetween(
                evalXpath("random-past-date(5,'days','2020-1-1')"),
                getDate(2020, 1, 1) - 5,
                getDate(2020, 1, 1))


        use(TimeCategory) {
            assertBetween(
                    evalXpath("random-past-date(5,'minutes')"),
                    now - 5.minutes,
                    now)

            assertBetween(
                    evalXpath("random-past-date(5,'minute')"),
                    now - 5.minutes,
                    now)

            assertBetween(
                    evalXpath("random-past-date(5,'second')"),
                    now - 5.seconds,
                    now)

            assertBetween(
                    evalXpath("random-past-date(5,'seconds')"),
                    now - 5.seconds,
                    now)

            assertBetween(
                    evalXpath("random-past-date(5,'hour')"),
                    now - 5.hours,
                    now)

            assertBetween(
                    evalXpath("random-past-date(5,'hours')"),
                    now - 5.hours,
                    now)


        }


    }

    static void assertBetween(def value, def start, def end) {
        assert value >= start && value <= end
    }

    static void assertNotBetween(def value, def start, def end) {
        assert !(value >= start && value <= end)
    }

}