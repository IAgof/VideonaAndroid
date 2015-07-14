/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.avrecorder.gles.FullFrameRect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.presentation.views.GLCameraEncoderView;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.CameraEffectClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.ColorEffectClickListener;

import org.lucasr.twowayview.TwoWayView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @author Álvaro Martínez Marco
 */

public class RecordFragment extends Fragment implements RecordView, ColorEffectClickListener,
        CameraEffectClickListener, AdapterView.OnItemSelectedListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordFragment"; //getClass().getSimpleName();
    /**
     * Activate some log_tag
     */
    private static final boolean VERBOSE = true;
    /**
     * Record Fragment
     */
    private static RecordFragment mFragment;
    /**
     * Record Presenter
     */
    private static RecordPresenter recordPresenter;        // Make static to survive Fragment re-creation
    /**
     * GLCameraEncoderView, openGL surfaceView
     */
    private GLCameraEncoderView mCameraView;

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
     * Rec point, animation
     */
    @InjectView(R.id.imageRecPoint)
    ImageView imageRecPoint;
    /**
     * Button to apply color effects
     */
    @InjectView(R.id.button_color_effect)
    ImageButton buttonColorEffect;
    /**
     * Button to apply camera effects
     */
    @InjectView(R.id.button_camera_effect)
    ImageButton buttonCameraEffect;
    /**
     * Button change camera
     */
    @InjectView((R.id.button_change_camera))
    ImageButton buttonChangeCamera;
    /**
     * Button flash mode
     */
    @InjectView(R.id.button_flash_mode)
    ImageButton buttonFlashMode;
    /**
     * Adapter to add images color effect
     */
    private ColorEffectAdapter colorEffectAdapter;
    /**
     * Position color effect pressed
     */
    public static int positionColorEffectPressed = 0;
    /**
     * Adapter to add images color effect
     */
    private CameraEffectAdapter cameraEffectAdapter;
    /**
     * Position camera effect pressed
     */
    public static int positionCameraEffectPressed = 0;
    /**
     * RelativeLayout to show and hide color effects
     */
    @InjectView(R.id.relativelayout_color_effect)
    RelativeLayout relativeLayoutColorEffect;
    /**
     * ListView to use horizontal adapter
     */
    @InjectView(R.id.listview_items_color_effect)
    TwoWayView listViewItemsColorEffect;
    /**
     * RelativeLayout to show and hide camera effects
     */
    @InjectView(R.id.relativelayout_camera_effect)
    RelativeLayout relativeLayoutCameraEffect;
    /**
     * ListView to use horizontal adapter
     */
    @InjectView(R.id.listview_items_camera_effect)
    TwoWayView listViewItemsCameraEffect;
    /**
     * CustomManualFocusView
     */
    @InjectView(R.id.customManualFocusView)
    CustomManualFocusView customManualFocusView;

    @InjectView(R.id.button_settings_camera)
    ImageButton buttonSettingsCamera;
    /**
     * Boolean, control show settings camera options
     */
    private boolean isSettingsCameraPressed = false;
    /**
     * Relative layout to show, hide camera options
     */
    @InjectView(R.id.linearLayoutRecordCameraOptions)
    LinearLayout linearLayoutRecordCameraOptions;

    /**
     * For lock the orientation to the current landscape.
     */
    public static boolean lockRotation = false;


    /**
     * Tracker google analytics
     */
    private Tracker tracker;

    // CountDown timer to prevent bugs
    private CountDownTimer countDownTimer;


    private SensorEventListener mOrientationListener = new SensorEventListener() {
        final int SENSOR_CONFIRMATION_THRESHOLD = 5;
        int[] confirmations = new int[2];
        int orientation = -1;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (getActivity() != null && getActivity().findViewById(R.id.rotateDeviceHint) != null) {
                //Log.i(LOG_TAG, "Sensor " + event.values[1]);
                if (event.values[1] > 10 || event.values[1] < -10) {
                    // Sensor noise. Ignore.
                } else if (event.values[1] < 5.5 && event.values[1] > -5.5) {
                    // Landscape
                    if (orientation != 1 && readingConfirmed(1)) {
                        if (recordPresenter != null && recordPresenter.getSessionConfig().isConvertingVerticalVideo()) {
                            if (event.values[0] > 0) {
                                recordPresenter.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.LANDSCAPE);
                            } else {
                                recordPresenter.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.UPSIDEDOWN_LANDSCAPE);
                            }
                        } else {
                            getActivity().findViewById(R.id.rotateDeviceHint).setVisibility(View.GONE);
                        }
                        orientation = 1;
                    }
                } else if (event.values[1] > 7.5 || event.values[1] < -7.5) {
                    // Portrait
                    if (orientation != 0 && readingConfirmed(0)) {
                        if (recordPresenter != null && recordPresenter.getSessionConfig().isConvertingVerticalVideo()) {
                            if (event.values[1] > 0) {
                                recordPresenter.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.VERTICAL);
                            } else {
                                recordPresenter.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.UPSIDEDOWN_VERTICAL);
                            }
                        } else {
                            getActivity().findViewById(R.id.rotateDeviceHint).setVisibility(View.VISIBLE);
                        }
                        orientation = 0;
                    }
                }
            }
        }

        /**
         * Determine if a sensor reading is trustworthy
         * based on a series of consistent readings
         */
        private boolean readingConfirmed(int orientation) {
            confirmations[orientation]++;
            confirmations[orientation == 0 ? 1 : 0] = 0;
            return confirmations[orientation] > SENSOR_CONFIRMATION_THRESHOLD;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    public RecordFragment() {
        // Required empty public constructor
        if (VERBOSE) Log.i(LOG_TAG, "construct");

    }

    public static RecordFragment getInstance() {
        if (mFragment == null) {
            // We haven't yet created a RecordFragment instance
            mFragment = recreateRecordFragment();
        } else if (recordPresenter != null && !recordPresenter.isRecording()) {
            // We have a leftover RecordFragment but it is not recording
            // Treat it as finished, and recreate
            mFragment = recreateRecordFragment();
        } else {
            Log.i(LOG_TAG, "Recycling recreateRecordFragment");
        }
        return mFragment;
    }

    private static RecordFragment recreateRecordFragment() {
        Log.i(LOG_TAG, "Recreating recreateRecordFragment");

        recordPresenter = null;
        return new RecordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (VERBOSE) Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setupRecordPresenter();

    }


    @Override
    public void onResume() {

        if (VERBOSE) Log.i(LOG_TAG, "onResume");
        super.onResume();

        if (recordPresenter != null) {

            recordPresenter.onHostActivityResumed();
            if (VERBOSE) Log.i(LOG_TAG, "onHostActivityResumed");

        } else {
            setupRecordPresenter();
        }

        startMonitoringOrientation();

        if (colorEffectAdapter != null) {
            colorEffectAdapter = null;
            recordPresenter.colorEffectClickListener();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause() RecordFragment");

        if (recordPresenter != null) {

            if(recordPresenter.isRecording()){

                recordPresenter.pauseRecording();

                showRecordFinished();
                stopChronometer();
                unLockNavigator();

                buttonRecord.setEnabled(true);
                buttonRecord.setImageAlpha(255); // (100%)
                chronometerRecord.setText("00:00");

                recordPresenter.onHostActivityPaused();

                return;

            }

            recordPresenter.onHostActivityPaused();
        }

        stopMonitoringOrientation();

    }



    @Override
    public void onStop(){
        super.onStop();
    }


    public void release(){
        recordPresenter.release();
        recordPresenter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recordPresenter != null && !recordPresenter.isRecording()) {
            recordPresenter.release();

            Log.d(LOG_TAG, "onDestroy() RecordFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (VERBOSE) Log.i(LOG_TAG, "onCreateView");

        VideonaApplication app = (VideonaApplication) getActivity().getApplication();
        tracker = app.getTracker();

        final View root;
        if (recordPresenter != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            root = inflater.inflate(R.layout.record_fragment, container, false);

            ButterKnife.inject(this, root);

            mCameraView = (GLCameraEncoderView) root.findViewById(R.id.cameraPreview);
            mCameraView.setKeepScreenOn(true);

            recordPresenter.initSessionConfig();

            recordPresenter.setPreviewDisplay(mCameraView);

            setupFilterSpinner(root);



        } else
            root = new View(container.getContext());


        // AutoFocusView
        customManualFocusView.onPreviewTouchEvent(getActivity().getApplicationContext());

        // Hide menu camera options
        linearLayoutRecordCameraOptions.setVisibility(View.GONE);

        //TODO String text chronometer default
        chronometerRecord.setText("00:00");


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private void setupFilterSpinner(View root) {
        Spinner spinner = (Spinner) root.findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.camera_filter_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    protected void setupRecordPresenter() {

        Log.d(LOG_TAG, "setupRecordPresenter");

        // By making the recorder static we can allow
        // recording to continue beyond this fragment's
        // lifecycle! That means the user can minimize the app
        // or even turn off the screen without interrupting the recording!
        // If you don't want this behavior, call stopRecording
        // on your Fragment/Activity's onStop()
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (recordPresenter == null) {

                try {

                    recordPresenter = new RecordPresenter(this);


                } catch (IOException e) {
                    Log.e(LOG_TAG, "Unable to create RecordPresenter. Could be trouble creating MediaCodec encoder.");
                    e.printStackTrace();
                }

            }
        }
    }



    /**
     *  Change camera listener
     */
    @OnClick (R.id.button_change_camera)
    public void buttonChangeCameraListener(){

        recordPresenter.requestOtherCamera();

        // Check flashMode and return to normal
        buttonFlashMode.setImageResource(R.drawable.activity_record_icon_flash_camera_normal);
        buttonSettinsCameraListener();

      //  mCameraView.setRotation(Surface.ROTATION_90);

    }

    /**
     * Camera flash mode listener
     */
    @OnClick(R.id.button_flash_mode)
    public void buttonFlashModeListener(){
        recordPresenter.onFlashModeTorchListener();
    }


    /**
     * Camera settings listener
     */
    @OnClick(R.id.button_settings_camera)
    public void buttonSettinsCameraListener(){


        if(isSettingsCameraPressed){
            // Hide menu
            linearLayoutRecordCameraOptions.setVisibility(View.GONE);
            buttonSettingsCamera.setImageResource(R.drawable.activity_record_settings_camera_normal);
            buttonSettingsCamera.setBackground(null);
            isSettingsCameraPressed = false;
        } else {
            // Show menu
            linearLayoutRecordCameraOptions.setVisibility(View.VISIBLE);
            buttonSettingsCamera.setImageResource(R.drawable.activity_record_settings_camera_pressed);
            buttonSettingsCamera.setBackgroundResource(R.color.transparent_palette_grey);
            isSettingsCameraPressed = true;
           // recordPresenter.settingsCameraListener();
        }
    }

    /**
     * Record button on click listener
     *
     * @return view
     */
    //TODO buttonRecordListener on Presenter
    @OnClick(R.id.button_record)
    public void buttonRecordListener() {
        Log.d(LOG_TAG, "buttonRecordListener");

        if (recordPresenter.isRecording()) {
            recordPresenter.stopRecording();

            countDownTimer = new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.recordError), Toast.LENGTH_SHORT).show();
                    reStartFragment();

                }
            }.start();

        } else {
            recordPresenter.startRecording();
            //stopMonitoringOrientation();
        }
    }

    private void restartRecordVideo() {

        //Restart original buttonRecord view
        buttonRecord.setEnabled(true);
        buttonRecord.setImageAlpha(255); // (100%)
        chronometerRecord.setText("00:00");

        recreateRecordFragment();
        setupRecordPresenter();

    }

    /**
     * Color effect on click listener
     */
    @OnClick(R.id.button_color_effect)
    public void colorEffectButtonListener() {
        recordPresenter.colorEffectClickListener();
    }

    /**
     * Camera effect on click listener
     */
    @OnClick(R.id.button_camera_effect)
    public void cameraEffectButtonListener() {
        recordPresenter.cameraEffectClickListener();
    }

    /**
     * Show list of effects
     *
     * @param effects
     */
    @Override
    public void showEffects(ArrayList<String> effects) {
        Log.d(LOG_TAG, "showEffects() RecordActivity");

        colorEffectAdapter = new ColorEffectAdapter(RecordFragment.this, effects);

        if (relativeLayoutColorEffect.isShown()) {

            relativeLayoutColorEffect.setVisibility(View.INVISIBLE);

            buttonColorEffect.setImageResource(R.drawable.common_icon_filters_normal);

            return;

        }
        relativeLayoutColorEffect.setVisibility(View.VISIBLE);
        buttonColorEffect.setImageResource(R.drawable.common_icon_filters_pressed);
        colorEffectAdapter.setViewClickColorEffectListener(RecordFragment.this);
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


    /**
     * Show list of effects
     *
     * @param effects
     */
    @Override
    public void showCameraEffects(ArrayList<String> effects) {
        Log.d(LOG_TAG, "showEffects() RecordActivity");

        cameraEffectAdapter = new CameraEffectAdapter(this, effects);

        if (relativeLayoutCameraEffect.isShown()) {

            relativeLayoutCameraEffect.setVisibility(View.INVISIBLE);

            buttonCameraEffect.setImageResource(R.drawable.activity_record_effects_bg);

            return;

        }
        relativeLayoutCameraEffect.setVisibility(View.VISIBLE);
        //buttonCameraEffect.setImageResource(R.drawable.common_icon_filters_pressed);
        cameraEffectAdapter.setViewClickCameraEffectListener(RecordFragment.this);
        listViewItemsCameraEffect.setAdapter(cameraEffectAdapter);
    }

    /**
     * Update view with effect selected
     *
     * @param cameraEffect
     */
    @Override
    public void showCameraEffectSelected(String cameraEffect) {
        Log.d(LOG_TAG, "showEffectSelected() RecordActivity");
        /// TODO apply animation effect
        cameraEffectAdapter.notifyDataSetChanged();
    }

    @Override
    public void navigateEditActivity() {

        Log.d(LOG_TAG, "navigateEditActivity() RecordActivity");

        countDownTimer.cancel();

        //Restart original buttonRecord view
        buttonRecord.setEnabled(true);
        buttonRecord.setImageAlpha(255); // (100%)
        chronometerRecord.setText("00:00");

        Intent edit = new Intent(getActivity(), EditActivity.class);
        startActivity(edit);

      //  reStartFragment();

    }

    @Override
    public void reStartFragment(){

        Fragment fg = this.getActivity().getFragmentManager().findFragmentById(R.id.record_fragment);

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(fg);
        ft.attach(fg);
        ft.commit();


    }


    @Override
    public void lockScreenRotation() {

        this.lockRotation = true;
    }

    @Override
    public void lockNavigator() {


    }

    @Override
    public void unLockNavigator() {

       // this.unLockNavigator();
    }

    @Override
    public void showSettingsCamera(boolean isChangeCameraSupported, boolean isFlashSupported) {

        showFlash(isFlashSupported);
        showChangeCamera(isChangeCameraSupported);
    }

    private void showFlash(boolean isFlashSupported) {
        if(isFlashSupported){
            buttonFlashMode.setVisibility(View.VISIBLE);
        } else {
            // ¿View.GONE or View.INVISIBLE? Double check
            buttonFlashMode.setVisibility(View.GONE);
        }
    }


    private void showChangeCamera(boolean isChangeCameraSupported) {

        Log.d(LOG_TAG, "showChangeCamera boolean " + isChangeCameraSupported);

        if(isChangeCameraSupported){
            buttonChangeCamera.setVisibility(View.VISIBLE);
        } else {
            // ¿View.GONE or View.INVISIBLE?
            buttonChangeCamera.setVisibility(View.GONE);
        }

    }


    @Override
    public void showFlashModeTorch(boolean mode) {

        if(mode){
            buttonFlashMode.setImageResource(R.drawable.activity_record_icon_flash_camera_pressed);
        } else {
            buttonFlashMode.setImageResource(R.drawable.activity_record_icon_flash_camera_normal);
        }

    }

    @Override
    public void showCamera(int cameraMode) {

        switch(cameraMode) {

            case 0:
                // Back camera
                buttonChangeCamera.setImageResource(R.drawable.activity_record_change_camera_normal);
                break;
            case 1:
                // Front camera
                buttonChangeCamera.setImageResource(R.drawable.activity_record_change_camera_normal);
                break;
            default:
                buttonChangeCamera.setImageResource(R.drawable.activity_record_change_camera_normal);
        }


    }


    /**
     * Force this fragment to stop recording.
     * Useful if your application wants to stop recording.
     * when a user leaves the Activity hosting this fragment
     */
    public void stopRecording() {
        if (recordPresenter.isRecording()) {
            recordPresenter.stopRecording();
            //amm recordPresenter.release();
        }
    }


    protected void startMonitoringOrientation() {
        if (getActivity() != null) {
            SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(mOrientationListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void stopMonitoringOrientation() {
        if (getActivity() != null) {
            View deviceHint = getActivity().findViewById(R.id.rotateDeviceHint);
            if (deviceHint != null) deviceHint.setVisibility(View.GONE);
            SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mOrientationListener);
        }
    }


    @Override
    public void showRecordStarted() {
        buttonRecord.setImageResource(R.drawable.activity_record_icon_stop_normal);
        buttonRecord.setImageAlpha(125); // (50%)
    }

    @Override
    public void showRecordFinished() {
        buttonRecord.setImageResource(R.drawable.activity_record_icon_rec_normal);
        buttonRecord.setEnabled(false);
        lockRotation = false;
        buttonFlashMode.setImageResource(R.drawable.activity_record_icon_flash_camera_normal);
    }

    @Override
    public void startChronometer() {

        setChronometer();
        chronometerRecord.start();
        // Activate animation rec
        imageRecPoint.setVisibility(View.VISIBLE);
        AnimationDrawable frameAnimation = (AnimationDrawable)imageRecPoint.getDrawable();
        frameAnimation.setCallback(imageRecPoint);
        frameAnimation.setVisible(true, true);

    }

    @Override
    public void stopChronometer() {

        chronometerRecord.stop();
        imageRecPoint.setVisibility(View.INVISIBLE);

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
                chronometerRecord.setText(mm + ":" + ss);
                RecordFragment.this.chronometerRecord.setText(mm + ":" + ss);

            }
        });
    }

    @Override
    public void showError() {

    }


    @Override
    public void onColorEffectClicked(ColorEffectAdapter adapter, String effectName, int position) {

        Log.d(LOG_TAG, "onColorEffectClicked() RecordActivity");
        positionCameraEffectPressed = position;
        adapter.notifyDataSetChanged();
        recordPresenter.setColorEffect(effectName);

    }

    @Override
    public void onCameraEffectClicked(CameraEffectAdapter adapter, String effectName, int position) {
        Log.d(LOG_TAG, "onCameraEffectClicked() RecordActivity");
        positionCameraEffectPressed = position;
        adapter.notifyDataSetChanged();
        recordPresenter.setCameraEffect(position);
    }


    /**
     * OnClick buttons, tracking Google Analytics
     */
    @OnClick({R.id.button_record, R.id.button_color_effect, R.id.button_flash_mode,
            R.id.button_settings_camera, R.id.button_change_camera})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id identifier of the clicked view
     */
    private void sendButtonTracked(int id) {
        String label;
        Log.d("RecordActivity", "sendButtonTracked");
        switch (id) {
            case R.id.button_record:
                label = "Capture ";
                break;
            case R.id.button_color_effect:
                label = "Show available effects";
                break;
            case R.id.button_change_camera:
                label = "Change camera";
                break;
            case R.id.button_flash_mode:
                label = "Flash camera";
                break;
            case R.id.button_settings_camera:
                label = "Settings camera";
                break;
            default:
                label = "Other";
        }

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getActivity().getApplication().getBaseContext()).dispatchLocalHits();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // hide selection text
        //     ((TextView)view).setText(null);
        // if you want you can change background here

        if (((String) parent.getTag()).compareTo("filter") == 0) {
            if(recordPresenter!= null) {
                recordPresenter.applyFilter(position);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
