/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;

import butterknife.OnClick;

/**
 * This class is used to show the about information.
 *
 * @author vlf
 * @since 04/05/2015
 */
public class AboutActivity extends Activity {

    /*CONFIG*/
    /**
     * Tracker google analytics
     */
    private VideonaApplication app;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        setContentView(R.layout.activity_about);
    }

    /**
     * Tracks when user clicks the link to go to Videona web page
     */
    @OnClick(R.id.videona_web)
    public void showVideonaWeb() {
        sendButtonTracked(R.id.videona_web);
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
                label = "other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("AboutActivity")
                .setAction("link clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }

}
