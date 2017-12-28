package com.omnitech.javarosa.console

import groovy.test.GroovyAssert
import org.junit.Test
import org.openxdata.markup.Converter
import org.openxdata.markup.FLAGS
import org.openxdata.markup.Form
import org.openxdata.markup.IFormElement

import static com.omnitech.javarosa.console.TestUtils.resourceText

class FormAutoFillTest implements LogConfig {

    @Test
    void textParsing() {

        def form = Converter.markup2Form(resourceText('/simpleform.mkp'))


        def result = TestUtils.formAutoFillFromMkp(form)
                              .autoFill()
                              .getSubmissionXml()


        assertAllNodeAnswers(form, result)


    }

    private static Iterable<IFormElement> assertAllNodeAnswers(Form form, String xml) {
        def node = new XmlParser().parseText(xml)

        def allNodes = node.'**'

        return form.allElementsWithIds.each { q ->
            assert allNodes.find { it.name() == q.id }.text()
        }
    }

    @Test
    void testWithValidationTelNumberShouldFail() {

        def mkp = '''## Form

                      @validif regex(., "07[0-9]{10}")
                      @message Provide right format
                      Tel Number'''

        def form = Converter.markup2Form(mkp)
        GroovyAssert.shouldFail(IllegalArgumentException) {
            TestUtils.formAutoFillFromMkp(form)
                     .autoFill()
                     .getSubmissionXml()
        }

    }

    @Test
    void testWithGenerexNumberShouldPass() {

        def mkp = '''
                      @bindxpath generex
                      ## Form

                      @validif regex(., "07[0-9]{10}")
                      @message Provide right format
                      @bind:generex random-regex('07[0-9]{10}')
                      Tel Number'''

        def form = Converter.markup2Form(mkp)
        def xml = TestUtils.formAutoFillFromMkp(form)
                           .autoFill()
                           .getSubmissionXml()

        assertAllNodeAnswers(form, xml)

    }

    @Test
    void textLargeForm() {

        def form = Converter.markup2Form(resourceText('/long-form1.mkp'), FLAGS.of(FLAGS.ODK_OXD_MODE))



        100.times {
            TestUtils.formAutoFillFromMkp(form)
                     .autoFill()
                     .getSubmissionXml()
        }


    }

//    @Test
//    void testSelectMulti() {
//        def items = [1, 2]
//
//        Map<Integer, Integer> stats = items.collectEntries { [it, 0] }
//        100.times {
//            def many = FormUtils.getRandomMany(items)
//            items.each { num -> if (many.contains(num)) stats[num] = stats[num] + 1 }
//        }
//
//        println(stats)
//    }
}