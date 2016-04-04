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

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.avrecorder.AVRecorder;
import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.event.CameraEncoderResetEvent;
import com.videonasocialmedia.avrecorder.event.CameraOpenedEvent;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.avrecorder.view.GLCameraEncoderView;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveVideosUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.domain.effects.GetEffectListUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideosRemovedFromProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.effects.OverlayEffect;
import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private SessionConfig config;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private AVRecorder recorder;
    private int recordedVideosNumber;
    private MixpanelAPI mixpanel;
    private Effect selectedShaderEffect;
    private Effect selectedOverlayEffect;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private VideoResolution videoResolution;
    private int videoBitrate;
    private String resolution;

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
    private boolean externalIntent;

    public RecordPresenter(Context context, RecordView recordView,
                           GLCameraEncoderView cameraPreview, SharedPreferences sharedPreferences, boolean externalIntent) {
        this.recordView = recordView;
        this.context = context;
        this.cameraPreview = cameraPreview;
        this.sharedPreferences = sharedPreferences;
        this.externalIntent = externalIntent;

        preferencesEditor = sharedPreferences.edit();
        exportProjectUseCase = new ExportProjectUseCase(this);
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeVideosUseCase = new RemoveVideosUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        recordedVideosNumber = 0;
        mixpanel = MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_TOKEN);
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

    private void hideInitialsButtons() {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.hideChronometer();
    }

    private SessionConfig getConfigFromPreferences(SharedPreferences sharedPreferences) {
        String destinationFolderPath = Constants.PATH_APP_TEMP;

        videoResolution = obtainResolutionFromPreferences(sharedPreferences);
        Log.d(LOG_TAG, " Config video resolution width: " + videoResolution.getWidth() + " x "
                + videoResolution.getHeight());

        videoBitrate = obtainVideoBitrateFromPreferences(sharedPreferences);

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
        if (resolution.compareTo(context.getString(R.string.good_value)) == 0) {
            videoResolution = new VideoResolution(VideoResolution.Resolution.HD1080);
        } else if (resolution.compareTo(context.getString(R.string.high_value)) == 0) {
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
        if (quality.compareTo(context.getString(R.string.good_value)) == 0) {
            videoQuality = new VideoQuality(VideoQuality.Quality.VERY_GOOD);
        } else if (quality.compareTo(context.getString(R.string.high_value)) == 0) {
            videoQuality = new VideoQuality(VideoQuality.Quality.EXCELLENT);
        } else {
            videoQuality = new VideoQuality(VideoQuality.Quality.GOOD);
        }
        return videoQuality.getVideoBitRate();
    }

    public String getResolution() {
        return videoResolution.getWidth() + "x" + videoResolution.getHeight();
    }

    public void onStart() {
        if (recorder.isReleased()) {
            cameraPreview.releaseCamera();
            initRecorder(context, cameraPreview, sharedPreferences);
        }
    }

    public void setGLSurface(GLCameraEncoderView cameraPreview) {
        this.cameraPreview = cameraPreview;
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
            recordView.enableShareButton();
        } else {
            recordView.hideRecordedVideoThumb();
            recordView.hideVideosRecordedNumber();
            recordView.disableShareButton();
        }
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
        stopRecord();
        recorder.onHostActivityPaused();
        Log.d(LOG_TAG, "pause presenter");
    }

    public void stopRecord() {
        if (recorder.isRecording()) {
            trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.STOP);
            recorder.stopRecording();
        }
        //TODO show a gif to indicate the process is running til the video is added to the project
    }

    /**
     * Sends button clicks to Mixpanel Analytics
     *
     * @param interaction
     * @param result
     */
    private void trackUserInteracted(String interaction, String result) {
        JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, context.getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recorder.isRecording());
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void resetRecorder() throws IOException {
        config = getConfigFromPreferences(sharedPreferences);
        recorder.reset(config);
    }

    private void startRecord() {
        mixpanel.timeEvent(AnalyticsConstants.VIDEO_RECORDED);
        trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.START);
        applyEffect(selectedShaderEffect);
        applyEffect(selectedOverlayEffect);

        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        recordView.showChronometer();
        recordView.hideSettings();
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.disableShareButton();
        firstTimeRecording = false;
    }

    public void applyEffect(Effect effect) {
        if (effect instanceof OverlayEffect) {
            recorder.removeOverlay();
            Drawable overlay = context.getResources().getDrawable(( (OverlayEffect) effect ).getResourceId());
            recorder.addOverlayFilter(overlay);
            selectedOverlayEffect = effect;
        } else {
            if (effect instanceof ShaderEffect) {
                int shaderId = ( (ShaderEffect) effect ).getResourceId();
                recorder.applyFilter(shaderId);
                selectedShaderEffect = effect;
            }
        }
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

    public void onEventMainThread(MuxerFinishedEvent e) {
        recordView.stopChronometer();
        String finalPath = moveVideoToMastersFolder();
        if (externalIntent) {
            recordView.finishActivityForResult(finalPath);
        } else {
            addVideoToProjectUseCase.addVideoToTrack(finalPath);
        }
    }

    private String moveVideoToMastersFolder() {
        File originalFile = new File(config.getOutputPath());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File destinationFile = new File(Constants.PATH_APP_MASTERS, fileName);
        originalFile.renameTo(destinationFile);
        updateTotalVideosRecorded();
        trackTotalVideosRecordedSuperProperty();
        double clipDuration = 0.0;
        try {
            clipDuration = Utils.getFileDuration(destinationFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        trackVideoRecorded(clipDuration);
        return destinationFile.getAbsolutePath();
    }

    private void updateTotalVideosRecorded() {
        int numTotalVideosRecorded = sharedPreferences
                .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED,
                ++numTotalVideosRecorded);
        preferencesEditor.commit();
    }

    private void trackTotalVideosRecordedSuperProperty() {
        JSONObject totalVideoRecordedSuperProperty = new JSONObject();
        int numPreviousVideosRecorded;
        try {
            numPreviousVideosRecorded =
                    mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_RECORDED);
        } catch (JSONException e) {
            numPreviousVideosRecorded = 0;
        }
        try {
            totalVideoRecordedSuperProperty.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                    ++numPreviousVideosRecorded);
            mixpanel.registerSuperProperties(totalVideoRecordedSuperProperty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trackVideoRecorded(Double clipDuration) {
        JSONObject videoRecordedProperties = new JSONObject();
        resolution = videoResolution.getWidth() + "x" + videoResolution.getHeight();
        int totalVideosRecorded = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        try {
            videoRecordedProperties.put(AnalyticsConstants.VIDEO_LENGTH, clipDuration);
            videoRecordedProperties.put(AnalyticsConstants.RESOLUTION, resolution);
            videoRecordedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                    totalVideosRecorded);
            mixpanel.track(AnalyticsConstants.VIDEO_RECORDED, videoRecordedProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        trackVideoRecordedUserTraits();
    }

    private void trackVideoRecordedUserTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 1);
        mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_RECORDED,
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
    }

    public void onEvent(AddMediaItemToTrackSuccessEvent e) {
        String path = e.videoAdded.getMediaPath();
        recordView.showRecordedVideoThumb(path);
        recordView.showRecordButton();
        recordView.enableShareButton();
        recordView.showVideosRecordedNumber(++recordedVideosNumber);
        recordView.showSettings();
        recordView.hideChronometer();
        recordView.reStartScreenRotation();
    }

    public void onEventMainThread(VideosRemovedFromProjectEvent e) {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.disableShareButton();
        recordedVideosNumber = 0;
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
        applyEffect(selectedShaderEffect);
        applyEffect(selectedOverlayEffect);
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

    public Effect getSelectedShaderEffect() {
        return selectedShaderEffect;
    }

    public Effect getSelectedOverlayEffect() {
        return selectedOverlayEffect;
    }

    public void removeEffect(Effect effect) {
        if (effect instanceof OverlayEffect) {
            recorder.removeOverlay();
            selectedOverlayEffect = null;
        } else {
            if (effect instanceof ShaderEffect) {
                recorder.applyFilter(Filters.FILTER_NONE);
                selectedShaderEffect = null;
            }
        }
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
