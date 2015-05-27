package com.videonasocialmedia.videona.presentation.mvp.views;

import android.preference.ListPreference;

import java.util.ArrayList;

/**
 * Created by jca on 9/3/15.
 */
public interface PreferencesView {
    void setAvailableSettings(ListPreference preference, ArrayList<Integer> listNames, ArrayList<Integer> listValues);
    void setDefaultSettings(ListPreference preference, Integer name, String key, ArrayList<Integer> listValues);
}
