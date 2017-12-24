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


    static String formatXML(String string) {
        def node = new XmlParser(false, false).parseText(string)
        return nodeToString(node)
    }

    static String nodeToString(Node node) {
        def writer = new StringWriter()
        def printWriter = writer.newPrintWriter()
        def wr = new XmlNodePrinter(printWriter).with {
            preserveWhitespace = true
            return it
        }
        wr.print(node)
        IOUtils.closeWithWarning(printWriter)
        return writer.toString()
    }
}


