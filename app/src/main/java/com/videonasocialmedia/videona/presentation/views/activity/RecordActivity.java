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
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.RecordFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */

public class RecordActivity extends Activity {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    @InjectView(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.activity_record_navigation_drawer)
    View navigatorView;
    /**
     * Record fragment
     */
    private RecordFragment recordFragment;
    /**
     * Boolean, register button back pressed to exit from app
     */
    private boolean buttonBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.inject(this);

        Log.d(LOG_TAG, "onCreate");

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState == null) {
            recordFragment = RecordFragment.getInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.record_fragment, recordFragment)
                    .commit();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                drawerLayout.closeDrawer(navigatorView);
            }
        }, 2000);
        Log.d(LOG_TAG, "onAttachedToWindow");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }


    public void lockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unLockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(navigatorView);
            return;
        }


        if (recordFragment != null) {
            recordFragment.stopRecording();
        }

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


    public void navigateToEdit() {

        Log.d(LOG_TAG, "navigateToEdit");

        Intent edit = new Intent(this, EditActivity.class);
        edit.putExtra("SHARE", false);
        startActivity(edit);
    }


}
