/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.videonasocialmedia.videona.domain.editor.RemoveVideosUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideosRemovedFromProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
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

public class RecordPresenter implements OnExportFinishedListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter";
    private boolean firstTimeRecording;
    private RecordView recordView;
    private ShareView shareView;
    private SessionConfig config;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private AVRecorder recorder;
    private int selectedEffect;
    private int recordedVideosNumber;
    private SharedPreferences sharedPreferences;

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
    private RemoveVideosUseCase removeVideosUseCase;

    public RecordPresenter(Context context, RecordView recordView, ShareView shareView,
                           GLCameraEncoderView cameraPreview, SharedPreferences sharedPreferences) {
        Log.d(LOG_TAG, "constructor presenter");
        this.recordView = recordView;
        this.shareView = shareView;
        this.context = context;
        this.cameraPreview = cameraPreview;
        this.sharedPreferences = sharedPreferences;

        exportProjectUseCase = new ExportProjectUseCase(this);
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeVideosUseCase = new RemoveVideosUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        selectedEffect = Filters.FILTER_NONE;
        recordedVideosNumber = 0;

        initRecorder(context, cameraPreview, sharedPreferences);

        hideInitialsButtons();
    }

    private void initRecorder(Context context, GLCameraEncoderView cameraPreview, SharedPreferences sharedPreferences) {

        config = getConfigFromPreferences(sharedPreferences);

        try {
            Drawable watermark = context.getResources().getDrawable(R.drawable.watermark720);
            recorder = new AVRecorder(config, watermark);
            recorder.setPreviewDisplay(cameraPreview);
            firstTimeRecording = true;
        } catch (IOException ioe) {
            Log.e("ERROR", "ERROR", ioe);
        }
    }

    private SessionConfig getConfigFromPreferences(SharedPreferences sharedPreferences) {
        String destinationFolderPath = Constants.PATH_APP_TEMP;

        VideoResolution videoResolution = obtainResolutionFromPreferences(sharedPreferences);
        Log.d(LOG_TAG, " Config video resolution width: " + videoResolution.getWidth() + " x "
                + videoResolution.getHeight());

        int videoBitrate = obtainVideoBitrateFromPreferences(sharedPreferences);

        //TODO make audio setting preferences
        int audioChannels = 1;
        int audioFrequency = 48000;
        int audioBitrate = 192 * 1000;

        return new SessionConfig(destinationFolderPath, videoResolution.getWidth(),
                videoResolution.getHeight(), videoBitrate, audioChannels, audioFrequency,
                audioBitrate);
    }

    private VideoResolution obtainResolutionFromPreferences(SharedPreferences sharedPreferences) {
        VideoResolution videoResolution;
        String key_resolution = ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION; //"list_preference_resolution";
        String resolution = sharedPreferences.getString(key_resolution, "");
        if (resolution.compareTo(context.getString(R.string.good_resolution_value)) == 0) {
            videoResolution = new VideoResolution(VideoResolution.Resolution.HD1080);
        } else if (resolution.compareTo(context.getString(R.string.high_resolution_value)) == 0) {
            videoResolution = new VideoResolution(VideoResolution.Resolution.HD4K);
        } else {
            videoResolution = new VideoResolution(VideoResolution.Resolution.HD720);
        }
        return videoResolution;
    }

    private int obtainVideoBitrateFromPreferences(SharedPreferences sharedPreferences) {

        String key_quality = ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY;
        String quality = sharedPreferences.getString(key_quality, "");
        Log.d(LOG_TAG, "list_preferences_quality " + quality);
        VideoQuality videoQuality;
        if (quality.compareTo(context.getString(R.string.good_quality_value)) == 0) {
            videoQuality = new VideoQuality(VideoQuality.Quality.VERY_GOOD);
        } else if (quality.compareTo(context.getString(R.string.high_quality_value)) == 0) {
            videoQuality = new VideoQuality(VideoQuality.Quality.EXCELLENT);
        } else {
            videoQuality = new VideoQuality(VideoQuality.Quality.GOOD);
        }
        return videoQuality.getVideoBitRate();
    }

    private void hideInitialsButtons() {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.hideChronometer();
    }

    public void onStart() {
        if (recorder.isReleased()) {
            cameraPreview.releaseCamera();
            initRecorder(context, cameraPreview, sharedPreferences);
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

        config = getConfigFromPreferences(sharedPreferences);
        recorder.reset(config);
    }

    public void startExport() {
        //editorView.showProgressDialog();
        //check VideoList is not empty, if true exportProjectUseCase
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList.size() > 0) {
            exportProjectUseCase.export();
        } else {
            recordView.hideProgressDialog();
            recordView.showMessage(R.string.add_videos_to_project);
        }
        //exportProjectUseCase.export();
    }

    public void removeMasterVideos() {
        removeVideosUseCase.removeMediaItemsFromProject();
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
        recordView.hideSettings();
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
        recordView.showSettings();
        recordView.hideChronometer();
        recordView.reStartScreenRotation();
    }

    public void onEventMainThread(VideosRemovedFromProjectEvent e) {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordedVideosNumber = 0;
        shareView.disableShareButton();
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

    public void applyEffect(int filterId) {
        selectedEffect = filterId;
        recorder.applyFilter(filterId);
    }

    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
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
