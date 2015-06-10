/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.record.RecordUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.utils.Constants;

import java.util.ArrayList;

public class RecordPresenter extends Presenter implements OnRecordEventListener,
        OnColorEffectListener, OnPreviewListener, OnOrientationEventListener,
        OnAddMediaFinishedListener, OnChangeCameraListener, OnSettingsCameraListener,
        OnFlashModeListener, OnRemoveMediaFinishedListener {

    /**
     * Record View
     */
    private final RecordView recordView;
    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    /**
     * Record Use Case
     */
    RecordUseCase recordUseCase;

    /**
     * Add Media to Project Use Case
     */
    AddVideoToProjectUseCase addVideoToProjectUseCase;

    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;

    /**
     * String path video recorded
     */
    private String pathVideoRecorded;

    /**
     * Boolean, control is recording file
     */
    private boolean isRecording = false;

    /**
     * Boolean, control flash mode, pressed or not
     */
    private boolean isFlahModeON = false;

    /*ANALYTICS*/
    private Tracker tracker;

    /**
     * Rotation View
     */
    private int rotationView;

    public RecordPresenter(RecordView recordView, Tracker tracker, Context applicationContext) {
        this.recordView = recordView;
        this.tracker = tracker;
        recordUseCase = new RecordUseCase(applicationContext);
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();
    }

    /**
     * Called when the presenter is initialized
     */
    @Override
    public void start(int displayOrientation) {
        recordUseCase.startPreview(this, displayOrientation);

        // Start with effect NONE, position 0
        // setEffect(Camera.Parameters.EFFECT_NONE);
        //  recordView.showEffectSelected(Camera.Parameters.EFFECT_NONE);
    }

    /**
     * Called when the presenter is stop, i.e when an activity
     * or a fragment finishes
     */
    @Override
    public void stop() {
        // recordUseCase.stopRecord(this);
    }

    /**
     * on Resume Presenter
     */
    public void onResume() {
        recordUseCase.onResume();
    }

    /**
     * on Pause Presenter
     */
    public void onPause() {
        recordUseCase.onPause();
    }

    /**
     * on Restart Presenter
     */
    public void onRestart() {
        recordUseCase.reStartPreview(this);
    }

    /**
     * on Stop Presenter
     */
    public void onStop() {
        if (recordUseCase != null) {
            if (isRecording) {
                recordUseCase.stopMediaRecorder(this);
            } else {
                recordUseCase.stopCamera();
            }
            //recordView.setEffect(Camera.Parameters.EFFECT_NONE);
            recordUseCase = null;
        }
    }

    /**
     * ReStart camera
     */
    public void reStartCamera(int displayOrientation){
        recordUseCase.startPreview(this, displayOrientation);
    }

    /**
     * Record Button pressed
     *
     * @deprecated
     */
    public void toggleRecord() {
        if (isRecording) {
            recordUseCase.stopRecord(this);
        } else {
            if (recordUseCase == null) {
                recordUseCase = new RecordUseCase(recordView.getContext());
            }
            recordUseCase.startRecord(this);
        }
    }

    /**
     * Effect Button pressed
     */
    public void effectClickListener() {
        recordUseCase.getAvailableEffects(this);
    }


    /**
     * Settings Camera listener
     */
    public void onSettingsCameraListener(){

        recordUseCase.getSettingsCamera(this);

    }

    /**
     * Flash Mode Torch pressed
     *
     * //TODO change to onFlashModeListener, support all kind of flash models.
     * // Get list of supportedFlashModel on SplashScreen and save to model.
     */
    public void onFlashModeTorchListener(){

        if(isFlahModeON){

            recordUseCase.removeFlashMode(Camera.Parameters.FLASH_MODE_OFF, this);
            isFlahModeON = false;

        } else {

            recordUseCase.addFlashMode(Camera.Parameters.FLASH_MODE_TORCH, this);
            isFlahModeON = true;
        }

    }

    public void onChangeCameraListener(){

        recordUseCase.changeCamera(this);

    }

    /**
     * Effect selected
     *
     * @param effect
     */
    public void setEffect(String effect) {
        recordUseCase.addAndroidCameraEffect(effect, this);
    }

    @Override
    public void onColorEffectAdded(String colorEffect, long time) {
        sendButtonTracked(colorEffect, time);
        recordView.showEffectSelected(colorEffect);
        Log.d(LOG_TAG, "onColorEffectAdded");
    }

    @Override
    public void onColorEffectRemoved(String colorEffect, long time) {
        recordView.showEffectSelected(colorEffect);
        Log.d(LOG_TAG, "onColorEffectRemoved");
    }

    @Override
    public void onColorEffectListRetrieved(ArrayList<String> effects) {
        recordView.showEffects(effects);
        Log.d(LOG_TAG, "onColorEffectListRetrieved");
    }


    @Override
    public void onRecordStarted() {
        recordView.showRecordStarted();
        recordView.lockScreenRotation();
        recordView.lockNavigator();
        recordView.startChronometer();
        isRecording = true;
        Log.d(LOG_TAG, "onRecordStarted");
    }

    @Override
    public void onRecordStopped(String videoPath) {
        recordView.showRecordFinished();
        recordView.stopChronometer();
        recordView.unLockNavigator();
        isRecording = false;
        Log.d(LOG_TAG, "onRecordStopped");
        clearProject();
        addVideoToProjectUseCase.addVideoToTrack(videoPath, this);
    }

    private void clearProject() {
        Project project=Project.getInstance(null, null, null);
        project.setMediaTrack(new MediaTrack());
        removeMusicFromProjectUseCase.removeAllMusic(0,this);
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {

    }

    @Override
    public void onRecordRestarted() {
        recordView.showRecordFinished();
        recordView.stopChronometer();
        isRecording = false;
        Log.d(LOG_TAG, "onRecordRestarted");
    }

    @Override
    public void onPreviewStarted(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView) {
        recordView.startPreview(cameraPreview, customManualFocusView);
        Log.d(LOG_TAG, "onPreviewStarted");
    }

    @Override
    public void onPreviewReStarted(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView) {
        recordView.stopPreview(cameraPreview, customManualFocusView);
        Log.d(LOG_TAG, "onPreviewReStarted");
    }

    @Override
    public void onOrientationChanged(int rotationView) {
        recordUseCase.setRotationView(rotationView);
        Log.d(LOG_TAG, "onOrientationChanged");
    }

    public void setRotationView(int rotationView){
        this.rotationView = rotationView;
        recordUseCase.setRotationView(rotationView);
        Log.d(LOG_TAG, "setRotationView " + rotationView);
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param colorEffect name of the clicked filter
     * @param time        time in which the effect is applied
     */
    private void sendButtonTracked(String colorEffect, long time) {
        String label;
        switch (colorEffect) {
            case Constants.COLOR_EFFECT_NONE:
                label = "AD0";
                break;
            case Constants.COLOR_EFFECT_AQUA:
                label = "AD1";
                break;
            case Constants.COLOR_EFFECT_BLACKBOARD:
                label = "AD2";
                break;
            case Constants.COLOR_EFFECT_EMBOSS:
                label = "AD3";
                break;
            case Constants.COLOR_EFFECT_MONO:
                label = "AD4";
                break;
            case Constants.COLOR_EFFECT_NEGATIVE:
                label = "AD5";
                break;
            case Constants.COLOR_EFFECT_NEON:
                label = "AD6";
                break;
            case Constants.COLOR_EFFECT_POSTERIZE:
                label = "AD7";
                break;
            case Constants.COLOR_EFFECT_SEPIA:
                label = "AD8";
                break;
            case Constants.COLOR_EFFECT_SKETCH:
                label = "AD9";
                break;
            case Constants.COLOR_EFFECT_SOLARIZE:
                label = "AD10";
                break;
            case Constants.COLOR_EFFECT_WHITEBOARD:
                label = "AD11";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("color effect applied")
                .setLabel(label)
                .setValue(time)
                .build());
    }

    @Override
    public void onAddMediaItemToTrackError() {
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media video) {
        recordView.navigateEditActivity();
    }

    @Override
    public void onRecordError() {

    }

    @Override
    public void onFlashModeTorchAdded() {

        recordView.showFlashModeTorch(true);
        Log.d(LOG_TAG, "onFlashModeTorchAdded");

    }

    @Override
    public void onFlashModeTorchRemoved() {

        recordView.showFlashModeTorch(false);
        Log.d(LOG_TAG, "onFlashModeTorchRemoved");

    }

    @Override
    public void onFlashModeTorchError(){
        //TODO something happened
        //recordView.showErrorMessage(String blablabla)
    }


    @Override
    public void onSettingsCameraSuccess(boolean isChangeCameraSupported,
                                        boolean isFlashSupported) {
        recordView.showSettingsCamera(isChangeCameraSupported, isFlashSupported);
        Log.d(LOG_TAG, "onSettingsCameraSuccess isChangeCameraSupported " + isChangeCameraSupported
         + " isFlashSupported " + isFlashSupported);
    }

    @Override
    public void onChangeCameraSuccess(int cameraMode){

        recordView.showCamera(cameraMode);
        Log.d(LOG_TAG, "onChangeCameraSuccess cameraMode " + cameraMode);
    }

    @Override
    public void onReleaseChangeCameraPreview(CameraPreview cameraPreview,
                                              CustomManualFocusView customManualFocusView, boolean
                                             autofocus){
        recordView.stopPreview(cameraPreview, customManualFocusView, autofocus);
        Log.d(LOG_TAG, "onReleaseChangeCameraPreview");
    }

    @Override
    public void onChangeCameraError(){

        //TODO something happened
        //recordView.showErrorMessage(String blablabla);
    }
}