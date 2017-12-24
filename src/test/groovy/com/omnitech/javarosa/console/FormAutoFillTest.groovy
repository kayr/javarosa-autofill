package com.omnitech.javarosa.console

import com.mifmif.common.regex.Generex
import org.junit.Test

class FormAutoFillTest  implements LogConfig{

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