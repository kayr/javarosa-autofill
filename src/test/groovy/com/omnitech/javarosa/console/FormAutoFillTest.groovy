package com.omnitech.javarosa.console

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

}