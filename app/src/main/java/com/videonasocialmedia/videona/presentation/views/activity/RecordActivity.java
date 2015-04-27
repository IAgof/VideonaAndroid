/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecordActivity extends Activity implements RecordView, ColorEffectClickListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Request code camera capture
     */
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    /**
     * Request code camera trim
     */
    private static final int CAMERA_EDIT_VIDEO_REQUEST_CODE = 300;

    /**
     * Request code video share
     */
    private static final int VIDEO_SHARE_REQUEST_CODE = 500;

    /**
     * Boolean, register button back pressed to exit from app
     */
    private boolean buttonBackPressed = false;

    /**
     * User private preferences
     */
    private static UserPreferences appPrefs;

    /**
     * Adapter to add images color effect
     */
    private ColorEffectAdapter colorEffectAdapter;

    /**
     * Uri, file url to store image/video
     */
    private Uri fileUri;

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

    /**
     * Tracker google analytics
     */
    private Tracker t;

    /**
     * RecordPresenter
     */
    private RecordPresenter recordPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        ButterKnife.inject(this);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Context context = getApplicationContext();
        appPrefs = new UserPreferences(context);

        // check if tempAV exists and jump to ShareActivity and trim Audio
        checkIsMusicOn();

        t = ((VideonaApplication) this.getApplication()).getTracker();

        recordPresenter = new RecordPresenter(this);

    }


    @Override
    protected void onStop() {
        super.onStop();

        recordPresenter.stop();

    }

    @Override
    protected void onStart() {
        super.onStart();

        recordPresenter.start();


    }

    @Override
    protected void onResume() {
        super.onResume();

        recordPresenter.onResume();

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_EDIT_VIDEO_REQUEST_CODE) {

            // Restart recordActivity
            onCreate(null);

        }

    }

    /**
     * Register back pressed to exit app
     */
    @Override
    public void onBackPressed() {

        buttonBackPressed = true;

        Toast.makeText(getApplicationContext(), getString(R.string.toast_exit), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && buttonBackPressed == true) {
            // do something on back.
            buttonBackPressed = false;
            Log.d(LOG_TAG, "onKeyDown");

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);

            return true;
        }

        buttonBackPressed = false;

        return super.onKeyDown(keyCode, event);

    }

    /**
     * User select effect
     *
     * @param adapter
     * @param effect
     */
    @Override
    public void onEffectClicked(ColorEffectAdapter adapter, Effect effect) {


        adapter.notifyDataSetChanged();

        recordPresenter.setEffect(effect);


    }

    @Override
    public Context getContext() {

       return this;
    }

    /**
     * Start preview
     *
     * @param camera
     * @param cameraPreview
     */
    @Override
    public void startPreview(Camera camera, CameraPreview cameraPreview){

            frameLayoutCameraPreview.addView(cameraPreview);
            frameLayoutCameraPreview.addView(new CustomManualFocusView(RecordActivity.this));

    }

    /**
     * Start recordVideo, show stop image
     */
    @Override
    public void startRecordVideo() {

        buttonRecord.setImageResource(R.drawable.activity_record_icon_stop_normal);
        buttonRecord.setImageAlpha(125); // (50%)
    }

    /**
     * Stop recordVideo, show record image
     */
    @Override
    public void stopRecordVideo() {

        buttonRecord.setImageResource(R.drawable.activity_record_icon_rec_normal);

    }


    /**
     * Start chronometer
     */
    @Override
    public void startChronometer() {

        chronometerRecord.start();
    }

    /**
     * Stop chronometer
     */
    @Override
    public void stopChronometer() {

        chronometerRecord.stop();

    }

    /**
     * Set chronometer with format 00:00
     */
    @Override
    public void setChronometer() {

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
     * @param adapter
     */
    @Override
    public void showEffects(ColorEffectAdapter adapter){

        colorEffectAdapter = adapter;

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
     * @param effect
     */
    @Override
    public void showEffectSelected(Effect effect){

        /// TODO apply animation effect

        colorEffectAdapter.notifyDataSetChanged();
    }


    /**
     * Color effect on click listener
     */
    @OnClick(R.id.button_color_effect)
    public void colorEffectButtonListener() {

        trackButtonClick(R.id.button_color_effect);

       recordPresenter.effectClickListener();

    }



    /**
     * Capture button on click listener
     *
     * @return view
     */
    @OnClick (R.id.button_record)
    public void buttonRecordListener() {


        trackButtonClick(R.id.button_record);

        recordPresenter.recordClickListener();

    }

    /**
     * Check if a temporal file is created and jump to ShareActivity
     * Needed to work with FFMpeg when add music to edition
     *
     */
    private void checkIsMusicOn() {

        Log.d(LOG_TAG, "RecordActivity checkIsMusicOn getIsMusicON " + appPrefs.getIsMusicON());

        File fTempAV = new File(Constants.VIDEO_MUSIC_TEMP_FILE);

        if (appPrefs.getIsMusicON() && fTempAV.exists()) {


            Intent share = new Intent();
            //  share.putExtra("MEDIA_OUTPUT", pathvideoTrim);
            share.setClass(RecordActivity.this, ShareActivity.class);
            startActivityForResult(share, VIDEO_SHARE_REQUEST_CODE);
        }

    }

    /**
     * Sends button clicks to GA
     *
     * @param id identifier of the clicked view
     */
    private void trackButtonClick(int id) {
        String label;
        switch (id) {
            case R.id.button_record:
                label = "capture ";
                break;
            case R.id.button_color_effect:
                label = "show available effects";
                break;
            case R.id.button5:
                //TODO change text
                label = "not a single clue of what should this button do";
                break;
            default:
                label = "other";
        }
        t.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }



}