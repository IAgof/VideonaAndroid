/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.SettingFragment;


/**
 * This class is used to manage the setting menu.
 */
public class SettingsActivity extends VideonaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_preferences, new SettingFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixpanel.timeEvent("Time in Settings Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mixpanel.track("Time in Settings Activity");
    }
}
