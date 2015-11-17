/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.qordoba.sdk.Qordoba;
import com.qordoba.sdk.common.QordobaContextWrapper;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.fragment.CriticalPermissionsDeniedDialogFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.JoinBetaDialogFragment;
import com.videonasocialmedia.videona.utils.PermissionConstants;

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
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
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

    protected final void closeApp() {
        Intent intent = new Intent(getApplicationContext(), InitAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    protected void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkContacts();
            checkNotificationsPermissions();
            checkStoragePermissions();
            checkAudioStoragePermissions();
            checkCameraPermissions();
            waitForCriticalPermissions();
        }
    }


    private void checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermissionConstants.PERMISSIONS_STORAGE,
                    PermissionConstants.REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermissionConstants.PERMISSIONS_CAMERA,
                    PermissionConstants.REQUEST_CAMERA);
        }
    }

    private void checkAudioStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, PermissionConstants.PERMISSIONS_AUDIO, PermissionConstants.REQUEST_AUDIO);
        }
    }

    private void checkContacts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermissionConstants.PERMISSIONS_CONTACTS,
                    PermissionConstants.REQUEST_CONTACTS);
        }
    }

    private void checkNotificationsPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_WAP_PUSH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermissionConstants.PERMISSIONS_NOTIFICATIONS,
                    PermissionConstants.REQUEST_NOTIFICATIONS);
        }
    }

    private void waitForCriticalPermissions() {
        while (!areCriticalPermissionsGranted()) {
            //just wait
            //TODO reimplement using handlers and semaphores
        }
    }

    private boolean areCriticalPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionConstants.REQUEST_EXTERNAL_STORAGE:
            case PermissionConstants.REQUEST_CAMERA:
            case PermissionConstants.REQUEST_AUDIO:
                if (!isPermissionGranted(grantResults)) {
                    showCloseAppDialog();
                }
        }
    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void showCloseAppDialog() {
        CriticalPermissionsDeniedDialogFragment dialog= new CriticalPermissionsDeniedDialogFragment();
        dialog.show(getFragmentManager(), "closeAppBecauseOfPermissionsDialog");

    }
}
