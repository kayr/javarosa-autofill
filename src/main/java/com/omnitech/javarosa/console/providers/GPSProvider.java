package com.omnitech.javarosa.console.providers;

import com.omnitech.javarosa.console.FormUtils;
import com.omnitech.javarosa.console.IAnswerProvider;
import org.javarosa.core.model.data.GeoPointData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

public class GPSProvider implements IAnswerProvider {
    @Override
    public IAnswerData acquire(FormEntryController fec, FormEntryPrompt prompt) {
        double longitude = Double.parseDouble(FormUtils.faker.address().longitude());
        double latitude  = Double.parseDouble(FormUtils.faker.address().latitude());
        return new GeoPointData(new double[]{latitude, longitude});
    }
}
