/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * This class is used to show the about information.
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
        String projectToken = "985b2aeb535dbc92b81fb5cce7ad1212";
        mixpanel = MixpanelAPI.getInstance(this, projectToken);
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}
