package com.videonasocialmedia.videona.presentation.views.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import java.util.ArrayList;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsBaseFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        PreferencesView {

    protected ListPreference resolutionPref;
    protected ListPreference qualityPref;
    protected PreferencesPresenter preferencesPresenter;
    protected Context context;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        context = getActivity().getApplicationContext();
        getPreferenceManager().setSharedPreferencesName(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        sharedPreferences = getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        resolutionPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION);
        qualityPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY);
        preferencesPresenter = new PreferencesPresenter(this, resolutionPref, qualityPref, context,
                sharedPreferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        preferencesPresenter.checkAvailablePreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Qordoba.updateScreen(getActivity());
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
        preference.setSummary(name);
        editor.putString(key, name);
        editor.commit();
    }

    @Override
    public void setPreference(ListPreference preference, String name) {
        preference.setValue(name);
        preference.setSummary(name);
    }

    @Override
    public void setSummary(String key, String value) {
        Preference preference = findPreference(key);
        preference.setSummary(value);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary(sharedPreferences.getString(key, ""));
    }

}
