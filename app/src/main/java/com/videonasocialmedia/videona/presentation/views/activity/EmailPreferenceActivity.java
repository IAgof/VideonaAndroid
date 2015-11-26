package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class EmailPreferenceActivity extends VideonaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.emailPreference);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
    }

}
