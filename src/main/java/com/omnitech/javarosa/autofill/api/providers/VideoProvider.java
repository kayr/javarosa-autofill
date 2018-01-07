package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.AutoFillException;
import com.omnitech.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class VideoProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        InputStream resourceAsStream = getClass().getResourceAsStream("/com/omnitech/javarosa/autofill/resouces/SampleVideo_720x480_1mb.mp4");

        Base64.Encoder encoder = Base64.getEncoder();

        try {
            String output = "data:video/mp4;base64," + encoder.encodeToString(IOUtils.getBytes(resourceAsStream));
            return new StringData(output);
        } catch (IOException e) {
            throw new AutoFillException(e);
        }

    }
}