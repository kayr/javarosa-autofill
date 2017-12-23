package com.omnitech.javarosa.console

import com.mifmif.common.regex.Generex
import org.junit.Test

class FormAutoFillTest {

    @Test
    void textParsing() {

        def string = FormAutoFill.fromResource("/Form1.xml")
                                 .autoFill()
                                 .getSubmissionXml()
        println(string)

    }

    @Test
    void textParsing2() {

        def string = FormAutoFill.fromResource("/simoshi.xml")
                                 .autoFill()
                                 .getSubmissionXml()
        println(string)

    }

    @Test
    void testGenerex() {


        generateFromString("[a-z]{4,10}[A-Z0-9]{4,10}")
        generateFromString("07[0-9]{8}")
        generateFromString(/[a-z]{4,10}\@[A-Z0-9]{4,10}/)
        generateFromString(/[a-z]{4,10}\@[A-Z0-9]{4,10}\.[A-Z]{2,5}/)
        generateFromString(/[A-Za-z0-9]{3,6}\@[A-Za-z0-9]{10}\.((com)|(net)|(ug))/)
        generateFromString(/[A-Za-z0-9._-]+\@[A-Za-z0-9]+\.[A-Za-z]{2,5}/)

    }

    String generateFromString(String regex) {
        Generex generex = new Generex(regex)
        def random = generex.random()


        println("${regex.padRight(80)}= $random")

//        generex.allMatchedStrings.forEach { println(it) }
        return random
    }

}