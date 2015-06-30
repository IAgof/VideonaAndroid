/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.RecordFragment;


/**
 * RecordActivity manages a single live record.
 */
//public class RecordActivity extends ImmersiveActivity {

    // Not immersive mode. Home buttons visibles
public class RecordActivity extends Activity {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private RecordFragment mFragment;

    /**
     * Boolean, register button back pressed to exit from app
     */
    private boolean buttonBackPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        if (savedInstanceState == null) {

            mFragment = RecordFragment.getInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();

        }

    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            mFragment.stopRecording();
        }

        //super.onBackPressed();

        if (buttonBackPressed) {
            buttonBackPressed = false;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            buttonBackPressed = true;
            Toast.makeText(getApplicationContext(), getString(R.string.toast_exit), Toast.LENGTH_SHORT).show();
        }
    }



}
