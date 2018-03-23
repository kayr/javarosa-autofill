package com.github.kayr.javarosa.autofill.api;

import org.javarosa.core.model.IFormElement;

public class AutoFillException extends RuntimeException {

    IFormElement element;

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

    public IFormElement getElement() {
        return element;
    }

    public AutoFillException setElement(IFormElement element) {
        this.element = element;
        return this;
    }
}
