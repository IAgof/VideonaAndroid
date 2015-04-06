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
import android.media.MediaRecorder;
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
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectList;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.UserPreferences;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecordActivity extends Activity implements RecordView, ColorEffectClickListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Camera Android
     */
    private Camera camera;

    /**
     * CameraPreview
     */
    private CameraPreview cameraPreview;

    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;

    /**
     * Boolean, control is recording file
     */
    private boolean isRecording = false;

    /**
     * Boolean is color effect selected
     */
    private static Boolean isColorEffect;

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
     * Uri, file url to store image/video
     */
    private Uri fileUri;

    /**
     * String absolute path video record file
     */
    private String videoRecordString;

    /**
     * Boolean, register button back pressed to exit from app
     */
    private boolean buttonBackPressed = false;

    /**
     * Int cameraId. Future use to multicamera
     */
    private int cameraId = 0;

    /**
     * Int media type video
     */
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * User private preferences
     */
    private static UserPreferences appPrefs;

    /**
     * Adapter to add images color effect
     */
    private ColorEffectAdapter colorEffectAdapter;

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

    /**
     * Position color effect pressed
     */
    public static int positionColorEffectPressed = 0;


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

        Log.d(LOG_TAG, "onResume");

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

    @Override
    public void onColorEffectClicked(int position) {

        colorEffect(position, camera);

        positionColorEffectPressed = position;

        colorEffectAdapter.notifyDataSetChanged();


    }

    @Override
    public Context getContext() {

       return this;
    }

    @Override
    public void startPreview(Camera camera, CameraPreview cameraPreview){


            frameLayoutCameraPreview.addView(cameraPreview);
            frameLayoutCameraPreview.addView(new CustomManualFocusView(RecordActivity.this));


    }

    @Override
    public void startRecordVideo() {

    }


    @Override
    public void stopRecordVideo() {

        // stop recording and release camera

        // buttonRecord, prevent error touch twice stop button
        buttonRecord.setEnabled(false);


        Intent trim = new Intent();
        trim.putExtra("MEDIA_OUTPUT", videoRecordString);
        trim.setClass(RecordActivity.this, EditActivity.class);
        //TODO create a video item with the path and add it to the edition project (using presenters and use cases)

        startActivityForResult(trim, CAMERA_EDIT_VIDEO_REQUEST_CODE);

    }

    @Override
    public void startChronometer() {

        chronometerRecord.start();
    }

    @Override
    public void stopChronometer() {

        chronometerRecord.stop();

        long chronometer = SystemClock.elapsedRealtime() - chronometerRecord.getBase();
        //  GetVideoRecordedUseCaseController.setRecordFileDuration(chronometer);

        recordPresenter.setRecordFileDuration(chronometer);

        Log.d(LOG_TAG, " chronometerRecord " + chronometer);
        Log.d(LOG_TAG, " chronometerRecord TimeUtils " + TimeUtils.toFormattedTime((int) chronometer));

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

    @Override
    public void colorEffect(int position, Camera camera) {


       // appPrefs.setColorEffect(CameraPreview.colorEffects.get(position));
        appPrefs.setColorEffect(colorFilter.get(position));

        Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(appPrefs.getColorEffect());

        camera.setParameters(parameters);
        // Color effects

        Log.d(LOG_TAG, "getIsColorEffect " + appPrefs.getIsColorEffect() + " filter " + appPrefs.getColorEffect());
        trackColorEffect(appPrefs.getColorEffect());

        recordPresenter.setColorEffect(appPrefs.getColorEffect(), chronometerRecord.getBase());

        // ¿?
        //appPrefs.setColorEffect(CameraPreview.colorEffects.get(0));

    }


    /**
     * Color effect on click listener
     */
    @OnClick(R.id.button_color_effect)
    public void colorEffectButtonListener() {

        trackButtonClick(R.id.button_color_effect);

        if (relativeLayoutColorEffect.isShown()) {

            relativeLayoutColorEffect.setVisibility(View.INVISIBLE);

            buttonColorEffect.setImageResource(R.drawable.common_icon_filters_normal);

            return;

        }

        relativeLayoutColorEffect.setVisibility(View.VISIBLE);

      // Sort by num colorEffectList

        //colorFilter = ColorEffectList.getColorEffectList();
        colorFilter = RecordPresenter.colorFilter;

        // Sort List
        colorFilter = ColorEffectList.sortColorEffectList(colorFilter);

        appPrefs.setColorEffect(colorFilter.get(0));

        colorEffectAdapter = new ColorEffectAdapter(RecordActivity.this, colorFilter);

        colorEffectAdapter.setViewClickListener(RecordActivity.this);

        listViewItemsColorEffect.setAdapter(colorEffectAdapter);

        buttonColorEffect.setImageResource(R.drawable.common_icon_filters_pressed);


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

           // fTempAV.delete();

           // appPrefs.setIsMusicON(false);

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

    /**
     * Tracks the effects applied by the user
     *
     * @param effect
     */
    private void trackColorEffect(String effect) {
        t.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("Color effect applied")
                .setCategory(effect)
                .build());
    }

}