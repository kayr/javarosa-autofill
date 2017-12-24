package com.omnitech.javarosa.console;

import org.javarosa.form.api.FormEntryPrompt;

import java.util.Objects;

public class ControlDataTypeKey {
    private int controlType, dataType;

    static ControlDataTypeKey with(int controlType, int dataType) {
        ControlDataTypeKey controlDataTypeKey = new ControlDataTypeKey();
        controlDataTypeKey.controlType = controlType;
        controlDataTypeKey.dataType = dataType;
        return controlDataTypeKey;
    }

    static ControlDataTypeKey fromPrompt(FormEntryPrompt prompt) {
        return with(prompt.getControlType(), prompt.getDataType());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ControlDataTypeKey that = (ControlDataTypeKey) o;

        return controlType == that.controlType && dataType == that.dataType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(controlType, dataType);
    }

    @Override
    public String toString() {
        return "ControlDataTypeKey{" + "controlType=" + controlType + ", dataType=" + dataType + '}';
    }
}
