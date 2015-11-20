/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * This class is used to show the about information.
 *
 * @author vlf
 * @since 04/05/2015
 */
public class AboutActivity extends VideonaActivity {

    /*CONFIG*/
    /**
     * Tracker google analytics
     */
    @InjectView(R.id.videona_version)
    TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        String version= BuildConfig.VERSION_NAME + "\n";
        versionName.setText(version);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixpanel.timeEvent("Time in About Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mixpanel.track("Time in About Activity");
    }

    /**
     * Tracks when user clicks the link to go to Videona web page
     */
    @OnClick({R.id.videona_web})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.videona_web:
                label = "Go to Videona web page from App";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("AboutActivity")
                .setAction("link clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }

}
