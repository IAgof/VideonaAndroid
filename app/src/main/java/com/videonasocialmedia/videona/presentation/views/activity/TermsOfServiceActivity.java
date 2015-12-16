/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;

/**
 * This class is used to show the about information.
 *
 * @author vlf
 * @since 04/05/2015
 */
public class TermsOfServiceActivity extends VideonaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixpanel.timeEvent("Time in Terms Of Service Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mixpanel.track("Time in Terms Of Service Activity");
    }

}
