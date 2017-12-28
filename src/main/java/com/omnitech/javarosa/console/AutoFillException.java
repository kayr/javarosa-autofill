package com.omnitech.javarosa.console;

public class AutoFillException extends RuntimeException {

    public AutoFillException() {
    }

    public AutoFillException(String message) {
        super(message);
    }

    public AutoFillException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoFillException(Throwable cause) {
        super(cause);
    }
}
