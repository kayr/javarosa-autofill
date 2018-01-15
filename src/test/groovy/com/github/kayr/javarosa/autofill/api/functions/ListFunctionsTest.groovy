package com.github.kayr.javarosa.autofill.api.functions

import com.github.kayr.javarosa.autofill.api.SmallFormEvaluator
import org.junit.Test

class ListFunctionsTest implements SmallFormEvaluator {

    @Test
    void testListFunctions() {
        assert evalXpath("list(1,2,3)") == [1, 2, 3]

        assert evalXpath("list-from-file('src/test/resources/data/options.txt')") ==
                ['1', '2', '3', '4', '5', '6', '7', '8', '0', '11']

        //noinspection GroovyInArgumentCheck
        assert evalXpath('list-random-remove(list(1,2,3,4,5))') in [1d, 2d, 3d, 4d, 5d]

        assert evalXpath('list-size(list(1,2,3))') == 3

        assert evalXpath('list-get-at(list(1,2,3),1)') == 2

        assert evalXpath('list-add(list(1,2,3),7,8)') == [1d, 2d, 3d, 7d, 8d]

        assert evalXpath('list-remove(list(1,2,3),2)') == [1d, 3d]

        assert evalXpath('list-remove-at(val("my-list",list(1,2,3)),0)') == 1d
        assert evalXpath('val("my-list")') == [2d, 3d]

    }
}
