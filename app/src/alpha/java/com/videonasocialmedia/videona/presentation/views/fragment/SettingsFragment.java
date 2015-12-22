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

import android.os.Bundle;
import android.preference.Preference;

/**
 * This class is used to manage the setting menu.
 */
public class SettingsFragment extends SettingsBaseFragment {

    private Preference joinBetaPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        joinBetaPref = findPreference("leave_beta");
        joinBetaPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new LeaveBetaDialogFragment().show(getFragmentManager(), "leaveBetaDialogFragment");
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesPresenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesPresenter);
    }
}