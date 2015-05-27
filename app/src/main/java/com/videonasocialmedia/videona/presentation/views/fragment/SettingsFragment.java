package com.videonasocialmedia.videona.presentation.views.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;

import java.util.ArrayList;

/**
 * Created by Veronica Lago Fominaya on 26/05/2015.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, PreferencesView {

    ListPreference resolutionPref;
    ListPreference qualityPref;
    PreferencesPresenter preferencesPresenter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        editor = sharedPreferences.edit();
        resolutionPref = (ListPreference) findPreference("list_preference_resolution");
        qualityPref = (ListPreference) findPreference("list_preference_quality");
        preferencesPresenter = new PreferencesPresenter(this, resolutionPref, qualityPref);
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
    public void setAvailableSettings(ListPreference preference, ArrayList<Integer> listNames, ArrayList<Integer> listValues) {

        int size = listNames.size();
        CharSequence entries[] = new String[size];
        CharSequence entryValues[] = new String[size];

        for (int i=0; i<size; i++) {
            entries[i] = getResources().getString(listNames.get(i));
            entryValues[i] = getResources().getString(listValues.get(i));
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
    }

    @Override
    public void setDefaultSettings(ListPreference preference, Integer name, String key, ArrayList<Integer> listValues) {
        // TODO hacer un if que entre cuando haya problemas con el de por defecto

        preference.setDefaultValue(getResources().getString(name));
        editor.putString(key, getResources().getString(name));
        editor.commit();
        editor.apply();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary(sharedPreferences.getString(key, ""));
    }

}
