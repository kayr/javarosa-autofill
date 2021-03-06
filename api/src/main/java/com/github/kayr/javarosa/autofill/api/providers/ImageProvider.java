package com.github.kayr.javarosa.autofill.api.providers;

import com.github.kayr.javarosa.autofill.api.AutoFillException;
import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.PointerAnswerData;
import org.javarosa.core.model.data.helper.BasicDataPointer;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ImageProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        InputStream resourceAsStream = getClass().getResourceAsStream("/com/github/kayr/javarosa/autofill/resouces/SampleJPGImage_50kbmb.jpg");
        try {
            BasicDataPointer dataPointer = new BasicDataPointer(UUID.randomUUID().toString() + ".jpg", IOUtils.getBytes(resourceAsStream));

            return new PointerAnswerData(dataPointer);

        } catch (IOException e) {
            throw new AutoFillException(e);
        }
    }
}
