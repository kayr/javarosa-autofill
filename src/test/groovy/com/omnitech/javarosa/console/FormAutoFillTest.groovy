package com.omnitech.javarosa.console

import org.junit.Test

import java.util.logging.Logger

public class FormAutoFillTest {

    @Test
    void textParsing() {

        Logger
        def main = FormAutoFill.fromResource("/Form1.xml")

        while (!main.hasEnded()) {
            main.next()
        }

        def xml = main.getSubmissionXml()
        def text = main.getSubmissionXmlString()

        println(text)


    }

}