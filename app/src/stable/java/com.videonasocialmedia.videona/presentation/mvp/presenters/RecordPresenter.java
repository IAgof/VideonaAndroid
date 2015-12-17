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

import java.io.IOException;
import java.util.List;

/**
 * @author Juan Javier Cabanas
 */

public class RecordPresenter extends RecordBasePresenter implements OnExportFinishedListener {

    private ShareView shareView;
    private SharedPreferences sharedPreferences;
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

    public void onStart() {
        if (recorder.isReleased()) {
            cameraPreview.releaseCamera();
            initRecorder(context, cameraPreview, sharedPreferences);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void resetRecorder() throws IOException {
        super.resetRecorder();
        config = getConfigFromPreferences(sharedPreferences);
        recorder.reset(config);
    }

    public void startRecord() {
        super.startRecord();
        recordView.hideSettings();
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
