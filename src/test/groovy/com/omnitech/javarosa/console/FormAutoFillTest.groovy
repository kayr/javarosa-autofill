package com.omnitech.javarosa.console

import groovy.test.GroovyAssert
import org.junit.Test
import org.openxdata.markup.Converter

import static com.omnitech.javarosa.console.TestUtils.resourceText

class FormAutoFillTest implements LogConfig {

    @Test
    void textParsing() {

        def form = Converter.markup2Form(resourceText('/simpleform.mkp'))


        def result = TestUtils.formAutoFillFromMkp("/simpleform.mkp")
                              .autoFill()
                              .getSubmissionXml()

        def node = new XmlParser().parseText(result)
        def allNodes = node.'**'

        form.allElementsWithIds.each { q ->
            assert allNodes.find { it.name() == q.id }.text()
        }


    }

    @Test
    void textParsing2() {

        GroovyAssert.shouldFail(IllegalArgumentException) {
            FormAutoFill.fromResource("/simoshi.xml")
                        .autoFill()
                        .getSubmissionXml()
        }

    }


}