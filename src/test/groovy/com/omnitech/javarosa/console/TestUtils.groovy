package com.omnitech.javarosa.console

import org.javarosa.core.model.FormDef
import org.javarosa.xform.util.XFormUtils
import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.Form

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

    static FormAutoFill formAutoFillFromMkp(String mkp) {
        return FormAutoFill.fromXml(mkp2Oxd(resourceText(mkp)))
    }

    static FormAutoFill formAutoFillFromMkp(Form mkp) {
        return FormAutoFill.fromXml(Converter.from(FORMAT.FORM).to(FORMAT.ODK).convert(mkp).toString())
    }

    static String mkpResource2Oxd(String mkp) {
        return mkp2Oxd(resourceText(mkp))
    }

    static String mkp2Oxd(String mkp) {
        return Converter.from(FORMAT.MARKUP).to(FORMAT.ODK).convert(mkp)
    }


}


