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


import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.videonasocialmedia.videona.domain.record.RecordUseCase;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;

import java.util.ArrayList;

public class RecordPresenter extends Presenter implements onRecordEventListener, onEffectListener, onPreviewListener {

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


    public RecordPresenter(RecordView recordView) {

        this.recordView = recordView;

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

        recordUseCase.stopRecord(this);

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
     * Record Button pressed
     */

    public void recordClickListener() {

        if (isRecording) {

           recordUseCase.startRecord(this);

        } else {

            recordUseCase.stopRecord(this);

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
    public void setEffect(Effect effect){

       // Check isEffectPressed, lastEffect, ... to add o remove effect

           recordUseCase.addEffect(effect, this);

           recordUseCase.removeEffect(effect, this);

    }

    @Override
    public void onEffectAdded(Effect effect, long time) {


        trackColorEffect(effect);

        recordView.showEffectSelected(effect);
    }

    @Override
    public void onEffectRemoved(Effect effect, long time) {

        recordView.showEffectSelected(effect);
    }

    @Override
    public void onEffectListRetrieved(ArrayList<String> effects) {

        ColorEffectAdapter colorEffectAdapter = new ColorEffectAdapter(RecordActivity.this, effects);

        recordView.showEffects(colorEffectAdapter);
    }


    @Override
    public void onRecordStarted() {

        recordView.startRecordVideo();
        //initialize chronometerRecord
        recordView.startChronometer();

        isRecording = true;

    }

    @Override
    public void onRecordStopped() {

        recordView.stopRecordVideo();
        recordView.stopChronometer();

        isRecording = false;

        ///TODO onRecordStopped navigate to EditActivity

     /*
        Intent edit = new Intent();
        edit.putExtra("MEDIA_OUTPUT", videoRecordString);
        edit.setClass(RecordActivity.this, EditActivity.class);

        startActivityForResult(edit, CAMERA_EDIT_VIDEO_REQUEST_CODE);

     */

    }

    @Override
    public void onPreviewStarted(Camera camera, CameraPreview cameraPreview){

        recordView.startPreview(camera, cameraPreview);
        recordView.setChronometer();

    }


    /**
     * Tracks the effects applied by the user
     *
     * @param effect
     */
    private void trackColorEffect(Effect effect) {
        t.send(new HitBuilders.EventBuilder()
                .setCategory("RecordActivity")
                .setAction("Color effect applied")
                .setCategory(effect.getName())
                .build());
    }

}
