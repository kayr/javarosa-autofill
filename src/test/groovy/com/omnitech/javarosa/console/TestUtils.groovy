package com.omnitech.javarosa.console

import org.javarosa.core.model.FormDef
import org.javarosa.xform.util.XFormUtils

class TestUtils {

    static String resourceText(String path) {
        return getClass().getResource(path).text
    }

    static FormDef parseFromResource(String resource) {
        return XFormUtils.getFormFromResource(resource)
    }


}


