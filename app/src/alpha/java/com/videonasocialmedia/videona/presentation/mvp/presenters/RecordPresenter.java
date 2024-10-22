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
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.auth.domain.usecase.LoginUser;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.EffectType;
import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;
import com.videonasocialmedia.videona.effects.domain.model.ShaderEffect;
import com.videonasocialmedia.videona.effects.domain.usecase.GetEffectListUseCase;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.promo.domain.GetPromos;
import com.videonasocialmedia.videona.promo.domain.GetPromosListener;
import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.RealmResults;

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
    private int recordedVideosNumber;
    private MixpanelAPI mixpanel;
    private Effect selectedShaderEffect;
    private Effect selectedOverlayEffect;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private Context context;
    private GLCameraView cameraPreview;
    private LoginUser loginUser;
    private boolean externalIntent;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public RecordPresenter(Context context, RecordView recordView,
                           GLCameraView cameraPreview, SharedPreferences sharedPreferences, boolean externalIntent) {
        this.recordView = recordView;
        this.context = context;
        this.cameraPreview = cameraPreview;
        this.sharedPreferences = sharedPreferences;
        this.externalIntent = externalIntent;
        loginUser = new LoginUser();
        preferencesEditor = sharedPreferences.edit();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        recordedVideosNumber = 0;
        mixpanel = MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_TOKEN);
        //initRecorder(cameraPreview);
    }

    public String getResolution() {
        return config.getVideoWidth() + "x" + config.getVideoHeight();
    }

    public void onStart() {
        if (recorder == null || recorder.isReleased()) {
//            cameraPreview.releaseCamera();
            initRecorder(cameraPreview);
        }
        hideInitialsButtons();
    }

    private void initRecorder(GLCameraView cameraPreview) {
        config = new SessionConfig(Constants.PATH_APP_TEMP);

        try {
            recorder = new AVRecorder(config);
            recorder.setPreviewDisplay(cameraPreview);
            firstTimeRecording = true;
        } catch (IOException ioe) {
            Log.e("ERROR", "ERROR", ioe);
        }
    }

    private void hideInitialsButtons() {
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.disableShareButton();
        recordView.hideChronometer();
    }

    public void onResume() {
        EventBus.getDefault().register(this);
        //recorder.onHostActivityResumed();
        if (!externalIntent)
            showThumbAndNumber();
        updateEffectLists();
        if (isAWolderUser())
            recorder.removeWatermark();
        else {
            Drawable watermark = context.getResources().getDrawable(R.drawable.watermark720);
            recorder.setWatermark(watermark);
        }
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

    private void updateEffectLists() {

        recordView.updateShaderEffectList(getShaderEffects());
        recordView.updateOverlayEffectList(getOverlayEffects());

    }

    private boolean isAWolderUser() {
        boolean result = false;
        final List<Promo> copyPromos = new ArrayList<>();
        GetPromos getPromosUseCase = new GetPromos();
        getPromosUseCase.getPromosByCampaign("wolder", new GetPromosListener() {
            @Override
            public void onPromosRetrieved(List<Promo> promos) {
                copyPromos.addAll(promos);
            }

            @Override
            public void onError() {

            }
        });

        for (Promo promo :
                copyPromos) {
            if (promo.getCampaign().compareToIgnoreCase("wolder") == 0 && promo.isActivated())
                result = true;
        }

        return result && loginUser.userIsLoggedIn();
    }

    public List<Effect> getShaderEffects() {

        return GetEffectListUseCase.getShaderEffectsList();
    }

    public List<Effect> getOverlayEffects() {

        return GetEffectListUseCase.getOverlayEffectsList();

    }


    public void onPause() {
        EventBus.getDefault().unregister(this);
        stopRecord();
        //recorder.onHostActivityPaused();
        Log.d(LOG_TAG, "pause presenter");
        recordView.hideProgressDialog();
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
        if (recorder.isRecording())
            recorder.stopRecording();
    }

    public void onDestroy() {
        recorder.release();
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

    private void startRecord() {
        mixpanel.timeEvent(AnalyticsConstants.VIDEO_RECORDED);
        trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.START);
        if (selectedShaderEffect != null)
            applyEffect(selectedShaderEffect);
        if (selectedOverlayEffect != null)
            applyEffect(selectedOverlayEffect);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        recordView.showChronometer();
        recordView.hideMenuOptions();
        recordView.hideRecordedVideoThumb();
        recordView.hideVideosRecordedNumber();
        recordView.disableShareButton();
        firstTimeRecording = false;
    }


    public void applyEffect(Effect effect){

        if(effect.getEffectType().compareTo(EffectType.OVERLAY.name()) == 0) {
            applyOverlayEffect((OverlayEffect) effect);
        } else {
            applyShaderEffect((ShaderEffect) effect);
        }
    }

    private void applyOverlayEffect(OverlayEffect effect){
        recorder.removeOverlay();
        Drawable overlay = context.getResources().getDrawable(effect.getResourceId());
        recorder.addOverlayFilter(overlay);
        selectedOverlayEffect = effect;
    }

    private void applyShaderEffect(ShaderEffect effect){
        int shaderId = effect.getResourceId();
        recorder.applyFilter(shaderId);
        selectedShaderEffect = effect;
    }

    public void discoverEffect() {
        GetEffectListUseCase.discoverOverlayEffects();
        recordView.updateOverlayEffectList(getOverlayEffects());
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
        Utils.addFileToVideoGallery(destinationFile.toString());
        int numTotalVideosRecorded = sharedPreferences
                .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED,
                ++numTotalVideosRecorded);
        preferencesEditor.commit();
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
        String resolution = config.getVideoWidth() + "x" + config.getVideoHeight();
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
        /* TODO: why do we update quality and resolution on video recorded?? This should be only updated in settings
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.RESOLUTION, sharedPreferences.getString(
                    AnalyticsConstants.RESOLUTION, resolution));
            userProfileProperties.put(AnalyticsConstants.QUALITY,
                    sharedPreferences.getInt(AnalyticsConstants.QUALITY, config.getVideoBitrate()));
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
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
        recordView.showMenuOptions();
        recordView.hideChronometer();
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

        if (selectedShaderEffect != null)
            applyEffect(selectedShaderEffect);
        if (selectedOverlayEffect != null)
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
        if (effect.getEffectType().compareTo(EffectType.OVERLAY.toString()) == 0) {
            recorder.removeOverlay();
            selectedOverlayEffect = null;
        } else {
            if (effect.getEffectType().compareTo(EffectType.SHADER.toString()) == 0) {
                recorder.applyFilter(Filters.FILTER_NONE);
                selectedShaderEffect = null;
            }
        }
    }

    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
    }


}
