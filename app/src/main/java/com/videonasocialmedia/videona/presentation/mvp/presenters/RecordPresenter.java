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


import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.domain.record.RecordUseCase;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;

import java.util.ArrayList;

public class RecordPresenter extends Presenter implements onRecordEventListener, onColorEffectListener, onPreviewListener {

    /**
     * Record Use Case
     */
    RecordUseCase recordUseCase;

    /**
     * Record View
     */
    private final RecordView recordView;

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Boolean, control is recording file
     */
    private boolean isRecording = false;

    /*ANALYTICS*/
    private Tracker tracker;



    public RecordPresenter(RecordView recordView, Tracker tracker) {

        this.recordView = recordView;

        this.tracker = tracker;

        recordUseCase = new RecordUseCase(recordView.getContext());

    }

    /**
     * Called when the presenter is initialized
     */
    @Override
    public void start() {

        recordUseCase.startPreview(this);

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
    public void onResume(){

        recordUseCase.onResume();


    }

    /**
     * on Pause Presenter
     */
    public void onPause(){

        recordUseCase.onPause();
    }

    /**
     * on Restart Presenter
     */
    public void onRestart(){

        recordUseCase.reStartPreview(this);
    }

    /**
     * on Stop Presenter
     */
    public void onStop(){

        if(recordUseCase == null) {

            Log.d(LOG_TAG, "recordUseCase null end of Activity");

        } else {

            if (isRecording) {

                recordUseCase.stopMediaRecorder(this);

            } else {

                recordUseCase.stopCamera();

            }
        }


    }

    /**
     * Record Button pressed
     */

    public void recordClickListener() {

        if (isRecording) {

           recordUseCase.stopRecord(this);

        } else {

            recordUseCase.startRecord(this);

        }

    }

    /**
     * Effect Button pressed
     */
    public void effectClickListener(){

        recordUseCase.getAvailableEffects(this);
    }

    /**
     *  Effect selected
     *
     * @param effect
     */
    public void setEffect(String effect){

           recordUseCase.addEffect(effect, this);

    }

    @Override
    public void onColorEffectAdded(String colorEffect, long time) {


        trackColorEffect(colorEffect, time);

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

      //  ColorEffectAdapter colorEffectAdapter = new ColorEffectAdapter(this, effects);

        recordView.showEffects(effects);

        Log.d(LOG_TAG, "onColorEffectListRetrieved");
    }


    @Override
    public void onRecordStarted() {

        recordView.startRecordVideo();

        //initialize chronometerRecord
        recordView.startChronometer();

        isRecording = true;

        Log.d(LOG_TAG, "onRecordStarted");

    }

    @Override
    public void onRecordStopped() {

        recordView.stopRecordVideo();
        recordView.stopChronometer();

        isRecording = false;

        ///TODO onRecordStopped, add media to Project useCase and navigate to EditActivity

        //recordUseCase.addMedia();

        recordView.navigateEditActivity(recordUseCase.getVideoRecordName());

        recordUseCase = null;

        Log.d(LOG_TAG, "onRecordStopped");

    }

    @Override
    public void onRecordRestarted(){


        recordView.stopRecordVideo();
        recordView.stopChronometer();

        isRecording = false;

    }

    @Override
    public void onPreviewStarted(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView){

        recordView.startPreview(cameraPreview, customManualFocusView);

        Log.d(LOG_TAG, "onPreviewStarted");
    }

    @Override
    public void onPreviewReStarted(CameraPreview cameraPreview, CustomManualFocusView customManualFocusView){

        recordView.stopPreview(cameraPreview, customManualFocusView);

        Log.d(LOG_TAG, "onPreviewReStarted");
    }

    /**
     * Tracks the effects applied by the user
     *
     * @param colorEffect
     */
    private void trackColorEffect(String colorEffect, long time) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("Color effect applied")
                .setCategory(colorEffect)
                .setValue(time)
                .build());
    }

}
