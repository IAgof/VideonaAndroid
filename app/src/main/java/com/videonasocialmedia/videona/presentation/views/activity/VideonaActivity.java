package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Context;

import com.qordoba.sdk.Qordoba;
import com.qordoba.sdk.common.QordobaContextWrapper;

/**
 * Videona base activity. Every
 */
public abstract class VideonaActivity extends Activity{
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new QordobaContextWrapper(this, newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Qordoba.updateScreen(this);
    }
}
