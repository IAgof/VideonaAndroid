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

        joinBetaPref = findPreference("join_beta");
        joinBetaPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new JoinBetaDialogFragment().show(getFragmentManager(), "joinBetaDialogFragment");
                return true;
            }
        });
    }

}
