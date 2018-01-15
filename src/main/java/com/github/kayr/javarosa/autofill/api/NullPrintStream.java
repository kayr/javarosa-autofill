package com.github.kayr.javarosa.autofill.api;

import java.io.OutputStream;
import java.io.PrintStream;

@SuppressWarnings("WeakerAccess")
public class NullPrintStream extends PrintStream {

    public NullPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) {

            }
        });
    }

}
