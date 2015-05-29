/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;

import java.util.ArrayList;

/**
 * This class is used to manage the setting menu.
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener,
        PreferencesView {

    ListPreference resolutionPref;
    ListPreference qualityPref;
    PreferencesPresenter preferencesPresenter;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        context = getActivity().getApplicationContext();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        editor = sharedPreferences.edit();
        resolutionPref = (ListPreference) findPreference("list_preference_resolution");
        qualityPref = (ListPreference) findPreference("list_preference_quality");
        preferencesPresenter = new PreferencesPresenter(this, resolutionPref, qualityPref, context,
                sharedPreferences);
        preferencesPresenter.checkAvailablePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        resolutionPref.setSummary(sharedPreferences.getString("list_preference_resolution", ""));
        qualityPref.setSummary(sharedPreferences.getString("list_preference_quality", ""));
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void setAvailablePreferences(ListPreference preference, ArrayList<String> listNames,
                                        ArrayList<String> listValues) {
        int size = listNames.size();
        CharSequence entries[] = new String[size];
        CharSequence entryValues[] = new String[size];
        for (int i=0; i<size; i++) {
            entries[i] = listNames.get(i);
            entryValues[i] = listValues.get(i);
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
    }

    @Override
    public void setDefaultPreference(ListPreference preference, String name, String key) {
        preference.setValue(name);
        editor.putString(key, name);
        editor.commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary(sharedPreferences.getString(key, ""));
    }
}
