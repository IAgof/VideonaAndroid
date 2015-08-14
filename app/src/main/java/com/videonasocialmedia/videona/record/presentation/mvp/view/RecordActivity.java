package com.videonasocialmedia.videona.record.presentation.mvp.view;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColorList;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;
import com.videonasocialmedia.videona.record.presentation.RecordView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jca on 10/8/15.
 */
public class RecordActivity extends Activity implements RecordView {


    @InjectView(R.id.button_record)
    ImageButton recordButton;
    @InjectView(R.id.chronometer_record)
    Chronometer chronometer;
    @InjectView(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @InjectView((R.id.button_change_camera))
    ImageButton toggleCameraButton;
    @InjectView(R.id.button_flash_mode)
    ImageButton flashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity_javi);
        ButterKnife.inject(this);

        hideCameraSettingsMenu();
        setupChronometer();
    }

    /**
     * Set chronometer with format 00:00
     */
    public void setupChronometer() {
        resetChronometer();
        chronometer.setOnChronometerTickListener(new android.widget.Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(android.widget.Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                String elapsedTime = formatElapsedTime(time);
                chronometer.setText(elapsedTime);
            }

            private String formatElapsedTime(long elapsedTime) {
                int h = (int) (elapsedTime / 3600000);
                int m = (int) (elapsedTime - h * 3600000) / 60000;
                int s = (int) (elapsedTime - h * 3600000 - m * 60000) / 1000;
                // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                return mm + ":" + ss;
            }
        });
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showStopButton() {
        recordButton.setImageResource(R.drawable.activity_record_icon_stop_normal);
        recordButton.setImageAlpha(125); // (50%)
    }

    @Override
    public void showRecButton() {
        recordButton.setImageResource(R.drawable.activity_record_icon_rec_normal);
        recordButton.setImageAlpha(255); // (100%)
    }

    @Override
    public void startChronometer() {
        resetChronometer();
        chronometer.start();
    }

    @Override
    public void stopChronometer() {
        chronometer.stop();
    }

    @Override
    public void showRecordingIndicator() {
        recordingIndicator.setVisibility(View.VISIBLE);
        AnimationDrawable frameAnimation = (AnimationDrawable) recordingIndicator.getDrawable();
        frameAnimation.setCallback(recordingIndicator);
        frameAnimation.setVisible(true, true);
    }

    @Override
    public void hideRecordingIndicator() {
        recordingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showFxList(List<CameraEffectFxList> effects) {

    }

    @Override
    public void hideFxList() {

    }

    @Override
    public void showFiltersList(List<CameraEffectColorList> filters) {

    }

    @Override
    public void hideFiltersList() {

    }

    @Override
    public void navigateEditActivity(String durationVideoRecorded) {

    }

    @Override
    public void lockScreenRotation() {

    }

    @Override
    public void lockNavigator() {

    }

    @Override
    public void unLockNavigator() {

    }

    @Override
    public void showCameraSettingsMenu(boolean isFlashSupported, boolean isChangeCameraSupported) {
        if (isFlashSupported)
            flashButton.setVisibility(View.VISIBLE);
        if (isChangeCameraSupported)
            toggleCameraButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCameraSettingsMenu() {
        flashButton.setVisibility(View.GONE);
        toggleCameraButton.setVisibility(View.GONE);
    }

    @Override
    public void showFlashActivated(boolean mode) {

    }

    @Override
    public void showError(String errorMesage) {

    }

    @Override
    public void showError(int errorMessageResourceId) {

    }
}
