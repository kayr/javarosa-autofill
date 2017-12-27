package com.omnitech.javarosa.console

import groovy.test.GroovyAssert
import org.junit.Test
import org.openxdata.markup.Converter

import static com.omnitech.javarosa.console.TestUtils.resourceText

class FormAutoFillTest implements LogConfig {

    @Test
    void textParsing() {

        def form = Converter.markup2Form(resourceText('/simpleform.mkp'))


        def result = TestUtils.formAutoFillFromMkp(form)
                              .autoFill()
                              .getSubmissionXml()

        def node = new XmlParser().parseText(result)
        def allNodes = node.'**'

        form.allElementsWithIds.each { q ->
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
        TestUtils.formAutoFillFromMkp(form)
                 .autoFill()
                 .getSubmissionXml()

    }


}