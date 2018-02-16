package com.github.kayr.javarosa.autofill.submission

import org.junit.Ignore
import org.junit.Test

class JavarosaClientTest {

    @Test
    @Ignore
    void submit() {

        def payload = new JavarosaClient().setUsername('admin')
                                          .setPassword('admin')
                                          .setServerUrl('http://localhost:8080/oxd/mpsubmit/odk/')



        payload.submit('<root id="20"/>')
    }
}