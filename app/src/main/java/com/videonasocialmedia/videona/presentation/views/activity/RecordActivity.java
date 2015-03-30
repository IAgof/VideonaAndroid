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
import android.view.View.OnClickListener;
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
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.UserPreferences;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecordActivity extends Activity implements RecordView, ColorEffectClickListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Camera Android
     */
    private Camera mCamera;

    /**
     * CameraPreview
     */
    private CameraPreview mCameraPreview;

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
    private static final int CAMERA_TRIM_VIDEO_REQUEST_CODE = 300;

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
    private RecordPresenter mRecordPresenter;

    /**
     * Position color effect pressed
     */
    public static int positionColorEffectPressed = 0;

    /**
     *
     *
     */
    public ArrayList<String> colorFilter;


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

        checkCameraHardware();

        // Create an instance of Camera
        if (mCamera == null)
            mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mCameraPreview = new CameraPreview(this, mCamera);

        //FrameLayout frameLayoutCameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        //frameLayoutCameraPreview.addView(mCameraPreview);


        frameLayoutCameraPreview.addView(mCameraPreview);
        frameLayoutCameraPreview.addView(new CustomManualFocusView(RecordActivity.this));

        buttonRecord.setOnClickListener(captureButtonListener());

        relativeLayoutColorEffect.setVisibility(View.INVISIBLE);

        buttonColorEffect.setOnClickListener(colorEffectButtonListener());

        t = ((VideonaApplication) this.getApplication()).getTracker();

        mRecordPresenter = new RecordPresenter(this);

    }


    @Override
    protected void onStop() {
        super.onStop();

        mRecordPresenter.stop();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mRecordPresenter.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume");

        if (mCamera == null) {

            mCamera = getCameraInstance();

            Log.d(LOG_TAG, "onResume height " + mCamera.getParameters().getPictureSize().height + " width " + mCamera.getParameters().getPictureSize().width);

            // Create our Preview view and set it as the content of our activity.
            mCameraPreview = new CameraPreview(this, mCamera);

            frameLayoutCameraPreview.addView(mCameraPreview);
            frameLayoutCameraPreview.addView(new CustomManualFocusView(RecordActivity.this));

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");

        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event.

        // Remove view. Prevent crash  Method called after release() in CameraPreview
        // FrameLayout frameLayoutCameraPreview = (FrameLayout)findViewById(R.id.camera_preview);
        //  frameLayoutCameraPreview.removeView(mCameraPreview);

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

        if (requestCode == CAMERA_TRIM_VIDEO_REQUEST_CODE) {

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


        colorEffect(position);

        positionColorEffectPressed = position;

        ///TODO Update view with color effect marked, background and text

        // Restart ColorEffect view.
      //  colorEffectAdapter = null;
      //  colorEffectAdapter = new ColorEffectAdapter(RecordActivity.this, new ColorEffectList().getColorEffectList());
      //  colorEffectAdapter.setViewClickListener(RecordActivity.this);

        //listViewItemsColorEffect.setAdapter(colorEffectAdapter);

        colorEffectAdapter.notifyDataSetChanged();


    }

    @Override
    public Context getContext() {

        return this;
    }


    /**
     * La funcionalidad no puede estar en la actividad
     * Tiene que estar en el dominio
     */
    @Override
    public void startRecordVideo() {

        // initialize video camera
        if (prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording

            mMediaRecorder.start();

            // inform the user that recording has started
            buttonRecord.setImageResource(R.drawable.activity_record_icon_stop_normal);  //setText("Stop");
            buttonRecord.setImageAlpha(125);
            isRecording = true;


            //initialize chronometerRecord
            startChronometer();

        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }

    }


    @Override
    public void stopRecordVideo() {

        // stop recording and release camera

        // buttonRecord, prevent error touch twice stop button
        buttonRecord.setEnabled(false);

        mMediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        //   buttonRecord.setImageResource(R.drawable.ic_btn_stop);  //setText("Capture");
        isRecording = false;

        stopChronometer();

        releaseCamera();

        Intent trim = new Intent();
        trim.putExtra("MEDIA_OUTPUT", videoRecordString);
        trim.setClass(RecordActivity.this, EditActivity.class);
        //TODO create a video item with the path and add it to the edition project (using presenters and use cases)

        startActivityForResult(trim, CAMERA_TRIM_VIDEO_REQUEST_CODE);

    }

    @Override
    public void startChronometer() {

        setChronometer();
        chronometerRecord.start();
    }

    @Override
    public void stopChronometer() {

        chronometerRecord.stop();

        long chronometer = SystemClock.elapsedRealtime() - chronometerRecord.getBase();
        //  GetVideoRecordedUseCaseController.setRecordFileDuration(chronometer);

        mRecordPresenter.setRecordFileDuration(chronometer);

        Log.d(LOG_TAG, " chronometerRecord " + chronometer);
        Log.d(LOG_TAG, " chronometerRecord TimeUtils " + TimeUtils.toFormattedTime((int) chronometer));

    }

    @Override
    public void colorEffect(int position) {


       // appPrefs.setColorEffect(CameraPreview.colorEffects.get(position));
        appPrefs.setColorEffect(colorFilter.get(position));

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setColorEffect(appPrefs.getColorEffect());

        mCamera.setParameters(parameters);
        // Color effects

        Log.d(LOG_TAG, "getIsColorEffect " + appPrefs.getIsColorEffect() + " filter " + appPrefs.getColorEffect());
        trackColorEffect(appPrefs.getColorEffect());

        mRecordPresenter.setColorEffect(appPrefs.getColorEffect());

        // ¿?
        //appPrefs.setColorEffect(CameraPreview.colorEffects.get(0));

    }


    /**
     * Color effect on click listener
     *
     * @return view
     */
    private OnClickListener colorEffectButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, " entro en buttonColorEffect");

                if (relativeLayoutColorEffect.isShown()) {

                    relativeLayoutColorEffect.setVisibility(View.INVISIBLE);

                    buttonColorEffect.setImageResource(R.drawable.common_icon_filters_normal);

                    return;

                }

                relativeLayoutColorEffect.setVisibility(View.VISIBLE);

                //appPrefs.setColorEffect(CameraPreview.colorEffects.get(0));


                // Sort by num colorEffectList


             //   colorEffectAdapter = new ColorEffectAdapter(RecordActivity.this, new ColorEffectList().getColorEffectList());

                colorFilter = sortColorEffectList();

                appPrefs.setColorEffect(colorFilter.get(0));

                colorEffectAdapter = new ColorEffectAdapter(RecordActivity.this, colorFilter);

                colorEffectAdapter.setViewClickListener(RecordActivity.this);

                listViewItemsColorEffect.setAdapter(colorEffectAdapter);

                buttonColorEffect.setImageResource(R.drawable.common_icon_filters_pressed);

            }

        };
    }


    public static ArrayList<String> sortColorEffectList() {

        ArrayList<String> colorEffectsSorted = new ArrayList<String>();

        ArrayList<String> colorEffects = new ColorEffectList().getColorEffectList();


       for(String effect: colorEffects) {

           Log.d("RecordActivity", " colorEffects " + effect);

           if (Constants.COLOR_EFFECT_NONE.compareTo(effect) == 0) {
               colorEffectsSorted.add(Constants.COLOR_EFFECT_NONE);

           }

       }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_AQUA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_AQUA);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_BLACKBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_BLACKBOARD);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_EMBOSS.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_EMBOSS);

            }
        }

        for(String effect: colorEffects) {
            if (Constants.COLOR_EFFECT_MONO.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_MONO);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_NEGATIVE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEGATIVE);

            }
        }

        for(String effect: colorEffects) {
            if (Constants.COLOR_EFFECT_NEON.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEON);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_POSTERIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_POSTERIZE);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SEPIA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SEPIA);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SKETCH.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SKETCH);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_SOLARIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SOLARIZE);

            }
        }

        for(String effect: colorEffects) {

            if (Constants.COLOR_EFFECT_WHITEBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_WHITEBOARD);

            }
        }


        return colorEffectsSorted;
    }

    /**
     * Capture button on click listener
     *
     * @return view
     */
    private View.OnClickListener captureButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trackButtonClick(v.getId());
                if (isRecording) {

                    stopRecordVideo();

                } else {

                    startRecordVideo();

                }
            }
        };
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
     * Set chronometer with format 00:00
     */
    private void setChronometer() {

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
     *  Open camera
     */
    private void openCamera() {

        // Create an instance of Camera
        if (mCamera == null)
            mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mCameraPreview = new CameraPreview(this, mCamera);

    }

    /**
     * Check camera hardware, num of cameras of device
     */
    private void checkCameraHardware() {

        int numCamera = mCamera.getNumberOfCameras();
        if (numCamera > 0) {
            Log.d(LOG_TAG, " checkCameraHardware numCamera " + numCamera);

            // Default back camera
            cameraId = 0;
        }

    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {

        Camera c = null;
        try {

            c = Camera.open(cameraId); // attempt to get a Camera instance

            // if (c != null) {
            Camera.Parameters params = c.getParameters();

            ///TODO Study bestSetPictureSize
            params.setPictureSize(ConfigUtils.VIDEO_SIZE_HEIGHT, ConfigUtils.VIDEO_SIZE_WIDTH);
            c.setParameters(params);

            Log.d(LOG_TAG, "getCameraInstance height " + c.getParameters().getPictureSize().height + " width " + c.getParameters().getPictureSize().width);

            // SetFrameRate
            params.setPreviewFrameRate(30);
            params.setPreviewFpsRange(30000, 30000);


            // Log CameraParameters info
            Log.d(LOG_TAG, " getParameters().getSupportedPreviewFrameRates() " );
            for(int framerate: c.getParameters().getSupportedPreviewFrameRates()){
                Log.d(LOG_TAG, " framerate: " + framerate);
            }

            Log.d(LOG_TAG, " getParameters(). getSupportedPreviewFpsRange ()() " );
            for(int[] fpsrange: c.getParameters().getSupportedPreviewFpsRange()){
                Log.d(LOG_TAG, " fpsrange: " + fpsrange[0] + " x " + fpsrange[1]);
            }



            //  }

        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
            // Camera is not available (in use or does not exist)
        }

        Log.d(LOG_TAG, " getCameraInstance camera " + c);



        return c; // returns null if camera is unavailable
    }

    /**
     * Prepare VideoRecorder.
     *
     * Set Audio and Video Settings, ConfigUtils
     *
     * Use Videona Profile to record Video
     *
     * @return boolean isPrepared
     */
    private boolean prepareVideoRecorder() {

        mMediaRecorder = new MediaRecorder();

        // Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
      //  mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // Audio
        mMediaRecorder.setAudioSamplingRate(ConfigUtils.AUDIO_SAMPLING_RATE);
        mMediaRecorder.setAudioChannels(ConfigUtils.AUDIO_CHANNELS);
        mMediaRecorder.setAudioEncodingBitRate(ConfigUtils.AUDIO_ENCODING_BIT_RATE);

        // Video

        mMediaRecorder.setVideoSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);
        mMediaRecorder.setVideoFrameRate(ConfigUtils.VIDEO_FRAME_RATE);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(ConfigUtils.VIDEO_ENCODING_BIT_RATE);

        // Set output file
        videoRecordString = mRecordPresenter.getRecordFileString();

        // Check if videoRecordString exists
        File f = new File(videoRecordString);
        if (f.exists()) {
            videoRecordString = videoRecordString + "1";
        }

        mMediaRecorder.setOutputFile(videoRecordString);

        // Set the frameLayoutCameraPreview output
        mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

        // Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("DEBUG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("DEBUG", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    /**
     *  Release MediaRecorder
     */
    private void releaseMediaRecorder() {

        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.setPreviewCallback(null);
            mCamera.lock();           // lock camera for later use
        }
    }

    /**
     *  Release Camera
     */
    private void releaseCamera() {

        if (mCamera != null) {
            isRecording = false;
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;

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