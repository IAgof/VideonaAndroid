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
import com.videonasocialmedia.avrecorder.event.CameraOpenedEvent;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas
 */

public class RecordPresenter implements OnExportFinishedListener, OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter";
    private boolean firstTimeRecording;
    private RecordView recordView;
    private SessionConfig config;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private AVRecorder recorder;
    private int selectedEffect;
    private int recordedVideosNumber;

    private Context context;
    private GLCameraEncoderView cameraPreview;
    /**
     * Export project use case
     */
    private ExportProjectUseCase exportProjectUseCase;
    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public RecordPresenter(Context context, RecordView recordView,
                           GLCameraEncoderView cameraPreview) {
        Log.d(LOG_TAG, "constructor presenter");
        this.recordView = recordView;
        this.context = context;
        this.cameraPreview = cameraPreview;

        exportProjectUseCase = new ExportProjectUseCase(this);
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        selectedEffect = Filters.FILTER_NONE;
        recordedVideosNumber = 0;

        initRecorder(context, cameraPreview);
    }

    private void initRecorder(Context context, GLCameraEncoderView cameraPreview) {
        config = new SessionConfig(Constants.PATH_APP_TEMP);
        try {
            recorder = new AVRecorder(config, context.getResources()
                    .getDrawable(R.drawable.watermark720));
            recorder.setPreviewDisplay(cameraPreview);
            firstTimeRecording = true;
        } catch (IOException ioe) {
            Log.e("ERROR", "ERROR", ioe);
        }
    }

    public void onStart() {
        if (recorder.isReleased()) {
            cameraPreview.releaseCamera();
            initRecorder(context, cameraPreview);
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

    public void onStop() {
        recorder.release();
    }

    public void onDestroy() {
        //recorder.release();
    }

    public void stopRecord() {
        if (recorder.isRecording())
            recorder.stopRecording();
        //TODO show a gif to indicate the process is running til the video is added to the project
    }

    public void requestRecord() {
        if (!recorder.isRecording()) {
            if (!firstTimeRecording) {
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

    public void startExport() {
        //editorView.showProgressDialog();
        //check VideoList is not empty, if true exportProjectUseCase
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        //exportProjectUseCase.export();
    }

    public void onEventMainThread(CameraEncoderResetEvent e) {
        startRecord();
    }

    public void onEventMainThread(CameraOpenedEvent e){

        Log.d(LOG_TAG, "camera opened, camera != null");
        //Calculate orientation, rotate if needed
        //recordView.unlockScreenRotation();
        if(firstTimeRecording){
            recordView.unlockScreenRotation();
        }

    }

    private void startRecord() {
        applyEffect(selectedEffect);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        firstTimeRecording = false;
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
        recordView.showRecordedVideoThumb(path);
        recordView.showRecordButton();
        recordView.showVideosRecordedNumber(++recordedVideosNumber);
        recordView.reStartScreenRotation();
    }

    public int getProjectDuration() {
        return Project.getInstance(null, null, null).getDuration();
    }

    public int getNumVideosOnProject() {
        return recordedVideosNumber;
    }

    public void changeCamera() {
        //TODO controlar el estado del flash

        int camera = recorder.requestOtherCamera();

        if (camera == 0) {
            recordView.showBackCameraSelected();

        } else {

            if (camera == 1) {
                recordView.showFrontCameraSelected();
            }
        }

        applyEffect(selectedEffect);

        checkFlashSupport();

    }

    public void checkFlashSupport() {

        // Check flash support
        int flashSupport = recorder.checkSupportFlash(); // 0 true, 1 false, 2 ignoring, not prepared

        Log.d(LOG_TAG, "checkSupportFlash flashSupport " + flashSupport);

        if(flashSupport == 0){
            recordView.showFlashSupported(true);
            Log.d(LOG_TAG, "checkSupportFlash flash Supported camera");
        } else {
            if(flashSupport == 1) {
                recordView.showFlashSupported(false);
                Log.d(LOG_TAG, "checkSupportFlash flash NOT Supported camera");
            }
        }
    }

    public void setFlashOff(){
        boolean on = recorder.setFlashOff();
        recordView.showFlashOn(on);

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

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        exportProjectUseCase.export();
    }

    @Override
    public void onNoVideosRetrieved() {
        recordView.hideProgressDialog();
        recordView.showMessage(R.string.add_videos_to_project);
    }

    @Override
    public void onExportError(String error) {
        recordView.hideProgressDialog();
        //TODO modify error message
        recordView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onExportSuccess(Video exportedVideo) {
        recordView.hideProgressDialog();
        recordView.goToShare(exportedVideo.getMediaPath());
    }
}
