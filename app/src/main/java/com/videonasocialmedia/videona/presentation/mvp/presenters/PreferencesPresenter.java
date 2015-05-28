/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;

import java.util.ArrayList;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter {

    Context context;
    SharedPreferences sharedPreferences;
    PreferencesView preferencesView;
    ListPreference resolutionPref;
    ListPreference qualityPref;
    boolean prueba = true;
    boolean prueba2 = false;

    public PreferencesPresenter(PreferencesView preferencesView, ListPreference resolutionPref,
                                ListPreference qualityPref, Context context,
                                SharedPreferences sharedPreferences) {
        this.preferencesView = preferencesView;
        this.resolutionPref = resolutionPref;
        this.qualityPref = qualityPref;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void checkAvailablePreferences() {
        checkAvailableResolution();
        checkAvailableQuality();
    }

    /**
     *
     */
    private void checkAvailableResolution() {
        ArrayList<String> resolutionNames = new ArrayList<>();
        ArrayList<String> resolutionValues = new ArrayList<>();
        String defaultResolution = null;
        String key = "list_preference_resolution";

        if (prueba2 == true) {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.low_resolution_value));
            defaultResolution = context.getResources().getString(R.string.low_resolution_value);
        }
        if (prueba == true) {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.good_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.good_resolution_value);
            }
        }
        if (prueba == true) {
            resolutionNames.add(context.getResources().getString(R.string.high_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.high_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.high_resolution_value);
            }
        }
        if (resolutionNames.size() > 0 && defaultResolution != null) {
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
            if (updateDefaultPreference(key, resolutionValues)) {
                preferencesView.setDefaultPreference(resolutionPref, defaultResolution, key);
            }
        } else {
            // TODO
        }
    }

    /**
     *
     */
    private void checkAvailableQuality() {
        ArrayList<String> qualityNames = new ArrayList<>();
        ArrayList<String> qualityValues = new ArrayList<>();
        String defaultQuality = null;
        String key = "list_preference_quality";

        if (prueba == true) {
            qualityNames.add(context.getResources().getString(R.string.low_resolution_name));
            qualityValues.add(context.getResources().getString(R.string.low_resolution_value));
            if (defaultQuality == null) {
                defaultQuality = context.getResources().getString(R.string.low_resolution_value);
            }
        }
        if (prueba == true) {
            qualityNames.add(context.getResources().getString(R.string.good_resolution_name));
            qualityValues.add(context.getResources().getString(R.string.good_resolution_value));
            defaultQuality = context.getResources().getString(R.string.good_resolution_value);
        }
        if (prueba == true) {
            qualityNames.add(context.getResources().getString(R.string.high_resolution_name));
            qualityValues.add(context.getResources().getString(R.string.high_resolution_value));
            if (defaultQuality == null) {
                defaultQuality = context.getResources().getString(R.string.high_resolution_value);
            }
        }
        if (qualityNames.size() > 0 && defaultQuality != null) {
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
            if (updateDefaultPreference(key, qualityValues)) {
                preferencesView.setDefaultPreference(qualityPref, defaultQuality, key);
            }
        } else {
            // TODO
        }
    }

    /**
     * Checks if the actual default value in shared preferences is supported by the device
     *
     * @param key
     * @param values
     * @return
     */
    private boolean updateDefaultPreference(String key, ArrayList<String> values) {
        boolean result = false;
        String actualDefaultValue = sharedPreferences.getString(key, "null");
        if (!values.contains(actualDefaultValue)) {
            result = true;
        }
        return result;
    }
}
