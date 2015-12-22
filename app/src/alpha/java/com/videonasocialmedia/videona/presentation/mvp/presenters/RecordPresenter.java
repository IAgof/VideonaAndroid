/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.videonasocialmedia.videona.domain.effects.GetEffectListUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.effects.OverlayEffect;
import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;
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

public class RecordPresenter {

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

    public RecordPresenter(Context context, RecordView recordView,
                           GLCameraEncoderView cameraPreview) {
        Log.d(LOG_TAG, "constructor presenter");
        this.recordView = recordView;
        this.context = context;
        this.cameraPreview = cameraPreview;

        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        selectedEffect = Filters.FILTER_NONE;
        recordedVideosNumber = 0;

        initRecorder(context, cameraPreview);

        hideInitialsButtons();
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

    private void hideInitialsButtons() {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.hideChronometer();
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
        showThumbAndNumber();
        Log.d(LOG_TAG, "resume presenter");
    }

    private void showThumbAndNumber() {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        final List mediaInProject = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (mediaInProject != null && mediaInProject.size() > 0) {
            int lastItemIndex = mediaInProject.size() - 1;
            final Video lastItem = (Video) mediaInProject.get(lastItemIndex);
            this.recordedVideosNumber = mediaInProject.size();
            recordView.showVideosRecordedNumber(recordedVideosNumber);
            recordView.showRecordedVideoThumb(lastItem.getMediaPath());
        } else {
            recordView.hideRecordedVideoThumb();
            recordView.hideVideosRecordedNumber();
        }
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

    public void onEventMainThread(CameraEncoderResetEvent e) {
        startRecord();
    }

    public void onEventMainThread(CameraOpenedEvent e) {

        Log.d(LOG_TAG, "camera opened, camera != null");
        //Calculate orientation, rotate if needed
        //recordView.unlockScreenRotation();
        if (firstTimeRecording) {
            recordView.unlockScreenRotation();
        }

    }

    private void startRecord() {
        applyEffect(selectedEffect);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        recordView.showChronometer();
        recordView.hideMenuOptions();
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        firstTimeRecording = false;

    }


    public void onEventMainThread(MuxerFinishedEvent e) {
        recordView.stopChronometer();
        String finalPath = moveVideoToMastersFolder();
        addVideoToProjectUseCase.addVideoToTrack(finalPath);
    }

    private String moveVideoToMastersFolder() {
        File originalFile = new File(config.getOutputPath());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File destinationFile = new File(Constants.PATH_APP_MASTERS, fileName);
        originalFile.renameTo(destinationFile);
        return destinationFile.getAbsolutePath();
    }

    public void onEvent(AddMediaItemToTrackSuccessEvent e) {
        String path = e.videoAdded.getMediaPath();
        recordView.showRecordedVideoThumb(path);
        recordView.showRecordButton();
        recordView.showVideosRecordedNumber(++recordedVideosNumber);
        recordView.showMenuOptions();
        recordView.hideChronometer();
        recordView.reStartScreenRotation();
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

        if (flashSupport == 0) {
            recordView.showFlashSupported(true);
            Log.d(LOG_TAG, "checkSupportFlash flash Supported camera");
        } else {
            if (flashSupport == 1) {
                recordView.showFlashSupported(false);
                Log.d(LOG_TAG, "checkSupportFlash flash NOT Supported camera");
            }
        }
    }

    public void setFlashOff() {
        boolean on = recorder.setFlashOff();
        recordView.showFlashOn(on);

    }

    public void toggleFlash() {
        boolean on = recorder.toggleFlash();
        recordView.showFlashOn(on);
    }

    /**
     * @deprecated
     * @param filterId
     */
    public void applyEffect(int filterId) {
        selectedEffect = filterId;
        recorder.applyFilter(filterId);
    }

    public void applyEffect(Effect effect){
        if (effect instanceof OverlayEffect){
            recorder.removeOverlay();
            Drawable overlay= context.getResources().getDrawable(((OverlayEffect) effect).getResourceId());
            recorder.addOverlayFilter(overlay);
        }
        else{
            int shaderId= ((ShaderEffect)effect).getResourceId();
            recorder.applyFilter(shaderId);
        }
    }

    public void removeEffect(Effect effect) {
        if (effect instanceof OverlayEffect)
            recorder.removeOverlay();
        else if (effect instanceof ShaderEffect)
            recorder.applyFilter(Filters.FILTER_NONE);
    }


    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
    }

    public List<Effect> getDistortionEffectList() {
        return GetEffectListUseCase.getDistortionEffectList();
    }

    public List<Effect> getColorEffectList() {
        return GetEffectListUseCase.getColorEffectList();
    }

    public List<Effect> getShaderEffectList() {
        return GetEffectListUseCase.getShaderEffectsList();
    }

    public List<Effect> getOverlayEffects() {
        return GetEffectListUseCase.getOverlayEffectsList();
    }

}
