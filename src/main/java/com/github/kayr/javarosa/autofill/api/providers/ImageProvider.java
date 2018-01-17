package com.github.kayr.javarosa.autofill.api.providers;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        InputStream resourceAsStream = getClass().getResourceAsStream("/com/github/kayr/javarosa/autofill/resouces/SampleJPGImage_50kbmb.jpg");

        Base64.Encoder encoder = Base64.getEncoder();

        try {
            String output = "data:image/jpg;base64," + encoder.encodeToString(IOUtils.getBytes(resourceAsStream));
            return new StringData(output);
        } catch (IOException e) {
            throw new AutoFillException(e);
        }
    }
}
