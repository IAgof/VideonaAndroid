/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.avrecorder.AVRecorder;
import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.event.CameraEncoderResetEvent;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas
 */

public class RecordPresenter {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter";
    boolean first;
    /**
     * RecordView
     */
    private RecordView recordView;
    /**
     * EncodingConfig, configure muxer and audio and video settings
     */
    private SessionConfig config;
    /**
     * Add Media to Project Use Case
     */
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private AVRecorder recorder;
    private int selectedEffect;
    private int recordedVideosNumber;

    public RecordPresenter(Context context, RecordView recordView,
                           GLCameraEncoderView cameraPreview) {

        Log.d(LOG_TAG, "constructor presenter");

        this.recordView = recordView;
        initRecorder(context, cameraPreview);
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        selectedEffect = Filters.FILTER_NONE;
        recordedVideosNumber = 0;
    }

    private void initRecorder(Context context, GLCameraEncoderView cameraPreview) {
        config = new SessionConfig(Constants.PATH_APP_TEMP);
        if (recorder == null) {
            try {
                recorder = new AVRecorder(config, context.getResources()
                        .getDrawable(R.drawable.watermark720));
                recorder.setPreviewDisplay(cameraPreview);
                first = true;
            } catch (IOException ioe) {
                Log.e("ERROR", "ERROR", ioe);
            }
        }
    }

    public void onResume() {
        EventBus.getDefault().register(this);
        recorder.onHostActivityResumed();
        Log.d(LOG_TAG, "resume presenter");
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
        stopRecord();
        recorder.onHostActivityPaused();
        Log.d(LOG_TAG, "pause presenter");
    }

    public void onStop(){
        //recorder.onHostActivityPaused();
    }

    public void onDestroy() {
        recorder.release();
    }



    public void stopRecord() {
        if (recorder.isRecording())
            recorder.stopRecording();
        //TODO show a gif to indicate the process is running til the video is added to the project
    }

    public void requestRecord() {
        if (!recorder.isRecording()) {
            if (!first) {
                try {
                    resetRecorder();
                } catch (IOException ioe) {
                    //recordView.showError();
                }
            } else {
                startRecord();
            }
        }
    }

    private void resetRecorder() throws IOException {
        config = new SessionConfig(Constants.PATH_APP_TEMP);
        recorder.reset(config);
    }

    public void onEventMainThread(CameraEncoderResetEvent e) {
        startRecord();
    }

    private void startRecord() {
        applyEffect(selectedEffect);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        first = false;
    }


    public void onEventMainThread(MuxerFinishedEvent e) {
        recordView.stopChronometer();
        String finalPath = moveVideoToMastersFolder();
        addVideoToProjectUseCase.addVideoToTrack(finalPath);
    }

    private String moveVideoToMastersFolder() {
        File originalFile = new File(config.getOutputPath());
        String fileName = originalFile.getName();
        if (fileName.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            fileName = "VID_" + timeStamp + ".mp4";
        }
        File destinationFile = new File(Constants.PATH_APP_MASTERS, fileName);
        originalFile.renameTo(destinationFile);
        return destinationFile.getAbsolutePath();
    }

    public void onEvent(AddMediaItemToTrackSuccessEvent e) {
        String path = e.videoAdded.getMediaPath();
        recordView.unlockScreenRotation();
        recordView.showRecordedVideoThumb(path);
        recordView.showRecordButton();
        recordView.showVideosRecordedNumber(++recordedVideosNumber);
    }

    public void changeCamera() {
        //TODO controlar el estado del flash
        int camera = recorder.requestOtherCamera();
        if (camera == 0)
            recordView.showBackCameraSelected();
        else if (camera == 1)
            recordView.showFrontCameraSelected();
        applyEffect(selectedEffect);
    }

    public void toggleFlash() {
        boolean on = recorder.toggleFlash();
        recordView.showFlashOn(on);
    }

    public void applyEffect(int filterId) {
        selectedEffect = filterId;
        recorder.applyFilter(filterId);
    }

    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
    }


}
