package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.avrecorder.AVRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.event.CameraEncoderResetEvent;
import com.videonasocialmedia.avrecorder.event.CameraOpenedEvent;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.effects.GetEffectListUseCase;
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
 * Created by Veronica Lago Fominaya on 17/12/2015.
 */
public abstract class RecordBasePresenter {

    /**
     * LOG_TAG
     */
    protected static final String LOG_TAG = "RecordPresenter";
    protected boolean firstTimeRecording;
    protected RecordView recordView;
    protected SessionConfig config;
    protected AddVideoToProjectUseCase addVideoToProjectUseCase;
    protected AVRecorder recorder;
    protected int selectedEffect;
    protected int recordedVideosNumber;
    protected Context context;
    protected GLCameraEncoderView cameraPreview;

    protected void hideInitialsButtons() {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.hideChronometer();
    }

    public void onResume() {
        EventBus.getDefault().register(this);
        recorder.onHostActivityResumed();
        showThumbAndNumber();
    }

    protected void showThumbAndNumber() {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        final List mediaInProject=getMediaListFromProjectUseCase.getMediaListFromProject();
        if (mediaInProject!=null && mediaInProject.size()>0){
            int lastItemIndex= mediaInProject.size()-1;
            final Video lastItem= (Video)mediaInProject.get(lastItemIndex);
            this.recordedVideosNumber=mediaInProject.size();
            recordView.showVideosRecordedNumber(recordedVideosNumber);
            recordView.showRecordedVideoThumb(lastItem.getMediaPath());
        }
        else{
            recordView.hideRecordedVideoThumb();
            recordView.hideVideosRecordedNumber();
        }
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
        stopRecord();
        recorder.onHostActivityPaused();
    }

    public void stopRecord() {
        if (recorder.isRecording())
            recorder.stopRecording();
        //TODO show a gif to indicate the process is running til the video is added to the project
    }

    public void onStop() {
        recorder.release();
    }

    public void onDestroy() {
        //recorder.release();
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

    protected void resetRecorder() throws IOException {}

    protected void startRecord() {
        applyEffect(selectedEffect);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        recordView.showChronometer();
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        firstTimeRecording = false;
    }

    public void applyEffect(int filterId) {
        selectedEffect = filterId;
        recorder.applyFilter(filterId);
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

    private void checkFlashSupport() {
        int flashSupport = recorder.checkSupportFlash(); // 0 true, 1 false, 2 ignoring, not prepared
        if (flashSupport == 0) {
            recordView.showFlashSupported(true);
        } else {
            if (flashSupport == 1) {
                recordView.showFlashSupported(false);
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

    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
    }

    public List<ShaderEffect> getDistortionEffectList() {
        return GetEffectListUseCase.getDistortionEffectList();
    }

    public List<ShaderEffect> getColorEffectList() {
        return GetEffectListUseCase.getColorEffectList();
    }

}
