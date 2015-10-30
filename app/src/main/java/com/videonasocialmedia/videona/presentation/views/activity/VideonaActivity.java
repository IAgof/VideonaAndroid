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

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.qordoba.sdk.Qordoba;
import com.qordoba.sdk.common.QordobaContextWrapper;
import com.videonasocialmedia.videona.BuildConfig;

/**
 * /**
 * Videona base activity. Every
 *
 * @author vlf
 * @since 04/05/2015
 */
public abstract class VideonaActivity extends Activity {

    protected MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO change this token in production mode
        //String projectToken = "1b1b93477de96457ce00058031524aed";
        mixpanel = MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN);
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
