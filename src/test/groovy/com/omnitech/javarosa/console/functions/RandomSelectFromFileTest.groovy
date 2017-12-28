package com.omnitech.javarosa.console.functions

import com.omnitech.javarosa.console.SmallFormEvaluator
import groovy.test.GroovyAssert
import org.junit.Test

import java.nio.file.NoSuchFileException

class RandomSelectFromFileTest implements SmallFormEvaluator {

    @Test
    void testRandomFromFile() {

        assert evalXpath("random-select-from-file('C:\\var\\code\\clincapture\\javarosa-console\\src\\test\\resources\\data\\options.txt')")
        assert evalXpath("random-select-from-file('src\\test\\resources\\data\\options.txt')")

        GroovyAssert.shouldFailWithCause(NoSuchFileException) { evalXpath("random-select-from-file('fake-file-path')") }


        setAutoFillBasePath("bad-base-path")
        GroovyAssert.shouldFailWithCause(NoSuchFileException) {
            evalXpath("random-select-from-file('src/test/resources/data/options.txt')")
        }
        resetBasePath()

        setAutoFillBasePath(".")
        assert evalXpath("random-select-from-file('src/test/resources/data/options.txt')")
        resetBasePath()


    }

    private String setAutoFillBasePath(String bas) {
        return System.setProperty(RandomSelectFromFile.AUTO_FILL_BASE_PATH, bas)
    }

    private void resetBasePath() {
        System.properties.remove(RandomSelectFromFile.AUTO_FILL_BASE_PATH)
    }


}