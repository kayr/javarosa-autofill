package com.omnitech.javarosa.autofill.api.providers;

import com.omnitech.javarosa.autofill.api.IAnswerProvider;
import com.omnitech.javarosa.autofill.api.functions.Fakers;
import org.javarosa.core.model.data.GeoPointData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public class GPSProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        double longitude = Double.parseDouble(Fakers.faker.address().longitude());
        double latitude  = Double.parseDouble(Fakers.faker.address().latitude());
        return new GeoPointData(new double[]{latitude, longitude});
    }
}
