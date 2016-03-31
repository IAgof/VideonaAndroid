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

import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.RemoveVideosUseCase;
import com.videonasocialmedia.videona.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import java.util.ArrayList;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Context context;
    private SharedPreferences sharedPreferences;
    private PreferencesView preferencesView;
    private ListPreference resolutionPref;
    private ListPreference qualityPref;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;

    /**
     * Constructor
     *
     * @param preferencesView
     * @param resolutionPref
     * @param qualityPref
     * @param context
     * @param sharedPreferences
     */
    public PreferencesPresenter(PreferencesView preferencesView, ListPreference resolutionPref,
                                ListPreference qualityPref, Context context,
                                SharedPreferences sharedPreferences) {
        this.preferencesView = preferencesView;
        this.resolutionPref = resolutionPref;
        this.qualityPref = qualityPref;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
    }

    /**
     * Checks the available preferences on the device
     */
    public void checkAvailablePreferences() {
        checkUserAccountData();
        checkAvailableResolution();
        checkAvailableQuality();
    }

    /**
     * Checks user preferences data
     */
    private void checkUserAccountData() {
        checkUserAccountPreference(ConfigPreferences.NAME);
        checkUserAccountPreference(ConfigPreferences.USERNAME);
        checkUserAccountPreference(ConfigPreferences.EMAIL);
    }

    private void checkUserAccountPreference(String key) {
        String data = sharedPreferences.getString(key, null);
        if (data != null && !data.isEmpty())
            preferencesView.setSummary(key, data);
    }


    /**
     * Checks supported resolutions on camera
     */
    private void checkAvailableResolution() {
        ArrayList<String> resolutionNames = new ArrayList<>();
        ArrayList<String> resolutionValues =  new ArrayList<>();
        String defaultResolution = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION; //"list_preference_resolution";
        boolean isPaidApp = true;
        // TODO check with flavors the app version (free/paid)

        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false) && isPaidApp) {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.low_value));
            defaultResolution = context.getResources().getString(R.string.low_value);
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false) && isPaidApp) {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.good_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.good_value);
            }
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false) && isPaidApp) {
            resolutionNames.add(context.getResources().getString(R.string.high_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.high_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.high_value);
            }
        }
        if (resolutionNames.size() > 0 && defaultResolution != null) {
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
            if (updateDefaultPreference(key, resolutionValues)) {
                preferencesView.setDefaultPreference(resolutionPref, defaultResolution, key);
            } else {
                preferencesView.setPreference(resolutionPref, sharedPreferences.getString(key, ""));
            }
        } else {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.low_value));
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
        }
    }

    /**
     * Checks supported qualities on camera
     */
    private void checkAvailableQuality() {
        ArrayList<String> qualityNames = new ArrayList<>();
        ArrayList<String> qualityValues = new ArrayList<>();
        String defaultQuality = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY; //"list_preference_quality";
        boolean isPaidApp = true;
        // TODO check with flavors the app version (free/paid)

        if (isPaidApp) {
            qualityNames.add(context.getResources().getString(R.string.low_quality_name));
            qualityValues.add(context.getResources().getString(R.string.low_value));
            if (defaultQuality == null) {
                defaultQuality = context.getResources().getString(R.string.low_value);
            }
        }
        if (isPaidApp) {
            qualityNames.add(context.getResources().getString(R.string.good_quality_name));
            qualityValues.add(context.getResources().getString(R.string.good_value));
            defaultQuality = context.getResources().getString(R.string.good_value);
        }
        if (isPaidApp) {
            qualityNames.add(context.getResources().getString(R.string.high_quality_name));
            qualityValues.add(context.getResources().getString(R.string.high_value));
            if (defaultQuality == null) {
                defaultQuality = context.getResources().getString(R.string.high_value);
            }
        }
        if (qualityNames.size() > 0 && defaultQuality != null) {
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
            if (updateDefaultPreference(key, qualityValues)) {
                preferencesView.setDefaultPreference(qualityPref, defaultQuality, key);
            } else {
                preferencesView.setPreference(qualityPref, sharedPreferences.getString(key, ""));
            }
        } else {
            qualityNames.add(context.getResources().getString(R.string.good_quality_name));
            qualityValues.add(context.getResources().getString(R.string.good_value));
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
        }
    }

    /**
     * Checks if the actual default value in shared preferences is supported by the device
     *
     * @param key the key of the shared preference
     * @param values the supported values for this preference
     * @return return true if the default value is not supported by the device, so update it
     */
    private boolean updateDefaultPreference(String key, ArrayList<String> values) {
        boolean result = false;
        String actualDefaultValue = sharedPreferences.getString(key, "");
        if (!values.contains(actualDefaultValue)) {
            result = true;
        }
        return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.compareTo(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY) == 0 ||
                key.compareTo(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION) == 0) {
            if (BuildConfig.FLAVOR.compareTo("stable") == 0) {
                RemoveVideosUseCase videoRemover = new RemoveVideosUseCase();
                videoRemover.removeMediaItemsFromProject();
            }
        }
    }

    public boolean checkIfWhatsappIsInstalled() {
        return obtainNetworksToShareUseCase.checkIfSocialNetworkIsInstalled("whatsapp");
    }

}
