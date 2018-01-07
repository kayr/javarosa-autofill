package com.omnitech.javarosa.autofill.submission

import org.apache.commons.lang3.text.StrTokenizer
import org.junit.Ignore
import org.junit.Test

 class JavarosaSubmitterTest {

    @Test
    @Ignore
     void submit() {

        def payload = new JavarosaSubmitter().setUsername('admin')
                                             .setPassword('admin')
                                             .setServerUrl('http://localhost:8080/oxd/mpsubmit/odk/')



        payload.submit('<root id="20"/>')
    }
}