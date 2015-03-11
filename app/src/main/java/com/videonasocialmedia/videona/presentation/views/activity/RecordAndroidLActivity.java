/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http: www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.Camera2VideoFragment;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;


import java.io.File;

public class RecordAndroidLActivity extends Activity {

    private static String LOG_TAG = "CameraActivity";

    private static UserPreferences appPrefs;

    private static final int VIDEO_SHARE_REQUEST_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }


        Context context = getApplicationContext();
        appPrefs = new UserPreferences(context);

        Log.d(LOG_TAG, "getIsMusicON " + appPrefs.getIsMusicON());

        File fTempAV = new File(Constants.videoMusicTempFile);

        if (appPrefs.getIsMusicON() && fTempAV.exists()) {

            Intent share = new Intent();
            //  share.putExtra("MEDIA_OUTPUT", pathvideoTrim);
            share.setClass(RecordAndroidLActivity.this, ShareActivity.class);
            startActivityForResult(share, VIDEO_SHARE_REQUEST_CODE);
        }
    }

}
