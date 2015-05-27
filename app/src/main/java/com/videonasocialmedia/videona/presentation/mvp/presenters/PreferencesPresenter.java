package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.preference.ListPreference;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;

import java.util.ArrayList;

/**
 * Created by jca on 9/3/15.
 */
public class PreferencesPresenter {

    PreferencesView preferencesView;
    ListPreference resolutionPref;
    ListPreference qualityPref;
    boolean prueba = true;

    public PreferencesPresenter(PreferencesView preferencesView, ListPreference resolutionPref,
                                ListPreference qualityPref){
        this.preferencesView=preferencesView;
        this.resolutionPref=resolutionPref;
        this.qualityPref=qualityPref;
    }

    public void checkAvailablePreferences() {
        checkAvailableResolution();
        checkAvailableQuality();
    }

    public void checkAvailableResolution() {
        ArrayList<Integer> resolutionNames = new ArrayList<>();
        ArrayList<Integer> resolutionValues = new ArrayList<>();
        int defaultResolution = 0;
        String key = "list_preference_resolution";

        if (prueba == true){
            resolutionNames.add(R.string.low_resolution_name);
            resolutionValues.add(R.string.low_resolution_value);
            defaultResolution = R.string.low_resolution_value;
        }
        if (prueba == true){
            resolutionNames.add(R.string.good_resolution_name);
            resolutionValues.add(R.string.good_resolution_value);
            if (defaultResolution == 0) {
                defaultResolution = R.string.good_resolution_value;
            }
        }
        if (prueba == true){
            resolutionNames.add(R.string.high_resolution_name);
            resolutionValues.add(R.string.high_resolution_value);
            if (defaultResolution == 0) {
                defaultResolution = R.string.high_resolution_value;
            }
        }
        if (resolutionNames.size() > 0 && defaultResolution != 0){
            preferencesView.setAvailableSettings(resolutionPref, resolutionNames, resolutionValues);
            preferencesView.setDefaultSettings(resolutionPref, defaultResolution, key, resolutionValues);
        } else {
            // TODO
        }
    }

    public void checkAvailableQuality() {

        ArrayList<Integer> qualityNames = new ArrayList<>();
        ArrayList<Integer> qualityValues = new ArrayList<>();
        int defaultQuality = 0;
        String key = "list_preference_quality";

        if (prueba == true){
            qualityNames.add(R.string.low_resolution_name);
            qualityValues.add(R.string.low_resolution_value);
            if (defaultQuality == 0) {
                defaultQuality = R.string.low_resolution_value;
            }
        }
        if (prueba == true){
            qualityNames.add(R.string.good_resolution_name);
            qualityValues.add(R.string.good_resolution_value);
            defaultQuality = R.string.good_resolution_value;
        }
        if (prueba == true){
            qualityNames.add(R.string.high_resolution_name);
            qualityValues.add(R.string.high_resolution_value);
            if (defaultQuality == 0) {
                defaultQuality = R.string.high_resolution_value;
            }
        }
        if (qualityNames.size() > 0 && defaultQuality != 0){
            preferencesView.setAvailableSettings(qualityPref, qualityNames, qualityValues);
            preferencesView.setDefaultSettings(qualityPref, defaultQuality, key, qualityValues);
        } else {
            // TODO
        }
    }
}
