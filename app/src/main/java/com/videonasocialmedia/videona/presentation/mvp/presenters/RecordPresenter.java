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
import com.videonasocialmedia.videona.utils.Constants;

import java.util.ArrayList;

public class RecordPresenter extends Presenter implements onRecordEventListener, onColorEffectListener, onPreviewListener {

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
        if (recordUseCase == null) {
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
    public void effectClickListener() {
        recordUseCase.getAvailableEffects(this);
    }

    /**
     * Effect selected
     *
     * @param effect
     */
    public void setEffect(String effect) {
        recordUseCase.addEffect(effect, this);
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
    public void onRecordRestarted() {
        recordView.stopRecordVideo();
        recordView.stopChronometer();
        isRecording = false;
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
}