/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.qordoba.sdk.Qordoba;
import com.qordoba.sdk.common.QordobaContextWrapper;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.VideonaApplication;

/**
 * /**
 * Videona base activity. Every
 *
 * @author vlf
 * @since 04/05/2015
 */
public abstract class VideonaActivity extends Activity {

    protected static final String ANDROID_PUSH_SENDER_ID = "783686583047";
    protected MixpanelAPI mixpanel;
    protected Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mixpanel = MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN);
        mixpanel.getPeople().identify(mixpanel.getPeople().getDistinctId());
        mixpanel.getPeople().initPushHandling(ANDROID_PUSH_SENDER_ID);
        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new QordobaContextWrapper(this, newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Qordoba.updateScreen(this);
    }
}
