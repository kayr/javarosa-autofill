package com.omnitech.javarosa.autofill.submission

import org.junit.Test

 class JavarosaSubmitterTest {

    @Test
     void submit() {

        def payload = new JavarosaSubmitter().setUsername('admin')
                                             .setPassword('admin')
                                             .setServerUrl('http://localhost:8080/oxd/mpsubmit/odk/')


        payload.submit('<root id="20"/>')
    }
}