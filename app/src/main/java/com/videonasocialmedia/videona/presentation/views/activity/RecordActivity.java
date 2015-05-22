/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * RecordActivity.
 *
 * Activity to preview and record video, apply color effects.
 *
 * When the video is recorded, navigate to EditActivity.
 *
 */
public class RecordActivity extends Activity implements RecordView, ColorEffectClickListener {

    /*VIEWS*/
    /**
     * Position color effect pressed
     */
    public static int positionColorEffectPressed = 0;
    /**
     * For lock the orientation to the current landscape.
     */
    public static boolean lockRotation = false;
    /**
     * Rotation preview
     */
    public static int rotationView;
    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    /**
     * Button to record video
     */
    @InjectView(R.id.button_record)
    ImageButton buttonRecord;
    /**
     * Chronometer, indicate time recording video
     */
    @InjectView(R.id.chronometer_record)
    Chronometer chronometerRecord;
    /**
     * Button to apply color effects
     */
    @InjectView(R.id.button_color_effect)
    ImageButton buttonColorEffect;
    /**
     * ListView to use horizontal adapter
     */
    @InjectView(R.id.listview_items_color_effect)
    TwoWayView listViewItemsColorEffect;
    /**
     * RelativeLayout to show and hide color effects
     */
    @InjectView(R.id.relativelayout_color_effect)
    RelativeLayout relativeLayoutColorEffect;
    /**
     * FrameLayout to camera preview
     */
    @InjectView(R.id.framelayout_camera_preview)
    ViewGroup frameLayoutCameraPreview;

    @InjectView(R.id.activity_record_drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.activity_record_navigation_drawer)
    View navigatorView;

    /**
     * OrientationEventListener
     */
    OrientationEventListener myOrientationEventListener;
    /**
     * Boolean, register button back pressed to exit from app
     */
    private boolean buttonBackPressed = false;
    /**
     * Adapter to add images color effect
     */
    private ColorEffectAdapter colorEffectAdapter;
    /**
     * RecordPresenter
     */
    private RecordPresenter recordPresenter;
    /**
     * Tracker google analytics
     */
    private Tracker tracker;
    /**
     * Boolean, control screenOrientation
     */
    private boolean detectScreenOrientation90 = false;
    /**
     * Boolean, control screenOrientation
     */
    private boolean detectScreenOrientation270 = false;
    /**
     * Screen orientation degrees
     */
    private int SCREEN_ORIENTATION_90 = 90;
    /**
     * Screen orientation degrees
     */
    private int SCREEN_ORIENTATION_270 = 270;
    /**
     * Preview display orientation
     */
    private int displayOrientation = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() RecordActivity");
        setContentView(R.layout.activity_record);
        ButterKnife.inject(this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        VideonaApplication app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
    }


    @Override
    protected void onStart() {
        super.onStart();

        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                drawerLayout.closeDrawer(navigatorView);
            }
        }, 3500);
        Log.d(LOG_TAG, "onStart() RecordActivity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart() RecordActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume() RecordActivity");
        recordPresenter = new RecordPresenter(this, tracker);
        /*
        if(recordPresenter != null) {
            recordPresenter.onResume();
        }
        */
        detectRotationView(this);
        recordPresenter.start(displayOrientation);
        if (colorEffectAdapter != null) {
            colorEffectAdapter = null;
            recordPresenter.effectClickListener();
        }
        recordPresenter.onResume();
        buttonRecord.setEnabled(true);
        chronometerRecord.setText("00:00");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause() RecordActivity");
        recordPresenter.onStop();
        recordPresenter = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop() RecordActivity");
    }

    /**
     * Register back pressed to exit app
     */
    @Override
    public void onBackPressed() {
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

    private void detectRotationView(Context context) {

        rotationView = getWindowManager().getDefaultDisplay().getRotation();

        if (rotationView == Surface.ROTATION_90) {
            detectScreenOrientation90 = true;

            displayOrientation = 0;

        }

        if (rotationView == Surface.ROTATION_270) {
            detectScreenOrientation270 = true;

            displayOrientation = 180;
        }

        Log.d(LOG_TAG, "rotationPreview " + rotationView);

        myOrientationEventListener = getOrientationEventListener(context);

        if (myOrientationEventListener.canDetectOrientation() == true) {
            Log.v("CameraPreview", " rotationPreview Can detect orientation");
            myOrientationEventListener.enable();
        } else {
            Log.v("CameraPreview", " rotationPreview Cannot detect orientation");
            myOrientationEventListener.disable();
        }

    }

    private OrientationEventListener getOrientationEventListener(Context context) {

        myOrientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                //  Log.d("CameraPreview", "onOrientationChanged " + orientation);
                if (lockRotation) { // || recordPresenter == null){
                    return;
                } else {

                    if (orientation == SCREEN_ORIENTATION_90) {
                        //  Log.d(LOG_TAG, "rotationPreview onOrientationChanged " + orientation);
                        if (detectScreenOrientation90) {
                            if (rotationView == Surface.ROTATION_90 && detectScreenOrientation270) {
                                return;
                            }
                            //    Log.d(LOG_TAG, "rotationPreview onOrientationChanged .*.*.*.*.*.* 90");
                            if (rotationView == Surface.ROTATION_270) {
                                rotationView = Surface.ROTATION_90;
                                if (recordPresenter != null) {
                                    if (recordPresenter != null) {
                                        recordPresenter.onOrientationChanged(rotationView);
                                    }
                                }
                                //      Log.d(LOG_TAG, "rotationPreview onOrientationChanged .*.*.*.*.*.* 90 rotation Preview 3");
                            } else {
                                if (rotationView == Surface.ROTATION_90) {
                                    rotationView = Surface.ROTATION_270;
                                    if (recordPresenter != null) {
                                        recordPresenter.onOrientationChanged(rotationView);
                                    }
                                    //  Log.d("CameraPreview", "rotationPreview onOrientationChanged .*.*.*.*.*.* 90 rotation Preview 1");
                                }
                            }
                            detectScreenOrientation90 = false;
                            detectScreenOrientation270 = true;
                        }
                    }
                    if (orientation == SCREEN_ORIENTATION_270) {
                        //  Log.d("CameraPreview", "rotationPreview onOrientationChanged " + orientation);
                        if (detectScreenOrientation270) {
                            //  Log.d("CameraPreview", "rotationPreview onOrientationChanged .*.*.*.*.*.* 270");
                            if (rotationView == Surface.ROTATION_270 && detectScreenOrientation90) {
                                return;
                            }
                            if (rotationView == Surface.ROTATION_270) {
                                rotationView = Surface.ROTATION_90;
                                if (recordPresenter != null) {
                                    recordPresenter.onOrientationChanged(rotationView);
                                }
                                //  Log.d("CameraPreview", "rotationPreview onOrientationChanged .*.*.*.*.*.* 270 rotation Preview 3");
                            } else {
                                if (rotationView == Surface.ROTATION_90) {
                                    rotationView = Surface.ROTATION_270;
                                    if (recordPresenter != null) {
                                        recordPresenter.onOrientationChanged(rotationView);
                                    }
                                    // Log.d("CameraPreview", "rotationPreview onOrientationChanged .*.*.*.*.*.* 270 rotation Preview 1");
                                }
                            }
                            detectScreenOrientation90 = true;
                            detectScreenOrientation270 = false;
                        }
                    }
                }
            }
        };
        return myOrientationEventListener;
    }


    /**
     * User select effect
     *
     * @param adapter
     * @param colorEffect
     */
    @Override
    public void onColorEffectClicked(ColorEffectAdapter adapter, String colorEffect, int position) {
        Log.d(LOG_TAG, "onColorEffectClicked() RecordActivity");
        positionColorEffectPressed = position;
        adapter.notifyDataSetChanged();
        recordPresenter.setEffect(colorEffect);
    }

    @Override
    public Context getContext() {
        Log.d(LOG_TAG, "getContext() RecordActivity");
        return this;
    }

    /**
     * Start preview
     *
     * @param cameraPreview
     */
    @Override
    public void startPreview(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView) {
        Log.d(LOG_TAG, "startPreview() RecordActivity");

        // detectRotationView(this);
        //  cameraPreview.setCameraOrientation(displayOrientation);
        frameLayoutCameraPreview.addView(cameraPreview);
        frameLayoutCameraPreview.addView(customManualFocusView);
        // Fix format chronometer 00:00. Do in xml, design
        chronometerRecord.setText("00:00");
        customManualFocusView.onPreviewTouchEvent(this);
        recordPresenter.effectClickListener();
    }

    @Override
    public void stopPreview(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView) {
        Log.d(LOG_TAG, "stopPreview() RecordActivity");
        frameLayoutCameraPreview.removeView(cameraPreview);
        frameLayoutCameraPreview.removeView(customManualFocusView);
    }

    /**
     * It shows that record has started
     */
    @Override
    public void showRecordStarted() {
        Log.d(LOG_TAG, "showRecordStarted() RecordActivity");
        buttonRecord.setImageResource(R.drawable.activity_record_icon_stop_normal);
        buttonRecord.setImageAlpha(125); // (50%)
    }

    /**
     * It shows that record has finished
     */
    @Override
    public void showRecordFinished() {
        Log.d(LOG_TAG, "showRecordFinished() RecordActivity");
        buttonRecord.setImageResource(R.drawable.activity_record_icon_rec_normal);
        buttonRecord.setEnabled(false);
    }

    @Override
    public void lockScreenRotation() {
        this.lockRotation = true;
    }


    /**
     * Start chronometer
     */
    @Override
    public void startChronometer() {
        Log.d(LOG_TAG, "startChronometer() RecordActivity");
        setChronometer();
        chronometerRecord.start();
    }

    /**
     * Stop chronometer
     */
    @Override
    public void stopChronometer() {
        Log.d(LOG_TAG, "stopChronometer() RecordActivity");
        chronometerRecord.stop();
    }

    /**
     * Set chronometer with format 00:00
     */
    public void setChronometer() {
        Log.d(LOG_TAG, "setChronometer() RecordActivity");
        chronometerRecord.setBase(SystemClock.elapsedRealtime());
        chronometerRecord.setOnChronometerTickListener(new android.widget.Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(android.widget.Chronometer chronometer) {

                long time = SystemClock.elapsedRealtime() - chronometer.getBase();

                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                // chronometerRecord.setText(hh+":"+mm+":"+ss);
                RecordActivity.this.chronometerRecord.setText(mm + ":" + ss);

            }
        });
    }

    /**
     * Show list of effects
     *
     * @param effects
     */
    @Override
    public void showEffects(ArrayList<String> effects) {
        Log.d(LOG_TAG, "showEffects() RecordActivity");
        //colorEffectAdapter = adapter;
        colorEffectAdapter = new ColorEffectAdapter(this, effects);

        if (relativeLayoutColorEffect.isShown()) {

            relativeLayoutColorEffect.setVisibility(View.INVISIBLE);

            buttonColorEffect.setImageResource(R.drawable.common_icon_filters_normal);

            return;

        }
        relativeLayoutColorEffect.setVisibility(View.VISIBLE);
        buttonColorEffect.setImageResource(R.drawable.common_icon_filters_pressed);
        colorEffectAdapter.setViewClickListener(RecordActivity.this);
        listViewItemsColorEffect.setAdapter(colorEffectAdapter);
    }

    /**
     * Update view with effect selected
     *
     * @param colorEffect
     */
    @Override
    public void showEffectSelected(String colorEffect) {
        Log.d(LOG_TAG, "showEffectSelected() RecordActivity");
        /// TODO apply animation effect
        colorEffectAdapter.notifyDataSetChanged();
    }

    @Override
    public void navigateEditActivity() {

        Log.d(LOG_TAG, "navigateEditActivity() RecordActivity");
        Intent edit = new Intent(RecordActivity.this, EditActivity.class);
        startActivity(edit);
    }

    /**
     * Color effect on click listener
     */
    @OnClick(R.id.button_color_effect)
    public void colorEffectButtonListener() {
        recordPresenter.effectClickListener();
    }

    /**
     * Capture button on click listener
     *
     * @return view
     */
    @OnClick(R.id.button_record)
    public void buttonRecordListener() {
        recordPresenter.recordClickListener();
    }

    @OnClick({R.id.button_record, R.id.button_color_effect})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    @Override
    public void lockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void unLockNavigator() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id identifier of the clicked view
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.button_record:
                label = "Capture ";
                break;
            case R.id.button_color_effect:
                label = "Show available effects";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }
}