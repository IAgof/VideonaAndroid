/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;

import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;

import com.videonasocialmedia.videona.avrecorder.SessionConfig;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.utils.Size;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnCameraEffectColorListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnCameraEffectFxListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnFlashModeListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRecordEventListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnSessionConfigListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnSettingsCameraListener;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColorList;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Álvaro Martínez Marco
 */

public class RecordUseCase {

    /**
     * Time to select color effect
     */
    private long timeColorEffect = 0;

    /**
     * Session config
     */
    private SessionConfig mConfig;

    /**
     * String outputLocation, save final video file.
     */
    private String outputLocation = "";

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     *
     */
     List<CameraEffectColorList> cameraEffectColorListAux;

    public RecordUseCase(){


    }

    /**
     * Init sessiong config.
     * Set video settings recording from Profile, project
     * //TODO * Set audio settings recording from Profile, project. Not implemented yet.
     * @param listener
     */
    public void initSessionConfig(OnSessionConfigListener listener){

        // Create a temp media file name to record video
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String fileName = "VID_" + timeStamp + ".mp4";
        String fileName = "VID_record_temp.mp4";
        File fTempRecord = new File(Constants.PATH_APP_TEMP, fileName);
        if(fTempRecord.exists()){
            fTempRecord.delete();
        }
        outputLocation = new File(Constants.PATH_APP_TEMP, fileName).getAbsolutePath();

        //Get data from Project, Profile
        Profile profile = Profile.getInstance(Profile.ProfileType.free);

        Size.Resolution resolution = profile.getResolution();
        Size videoSize = new Size(resolution);

        VideoQuality.Quality quality = profile.getVideoQuality();
        VideoQuality videoQuality = new VideoQuality (quality);

        Log.d(LOG_TAG, " Profile resolution " + resolution + " quality " + quality);


        // TODO, get audio data from Profile
        /*
        mConfig = new SessionConfig.Builder(outputLocation)
                .withVideoBitrate(videoQuality.getVideoBitRate())
                .withVideoResolution(videoSize.getWidth(), videoSize.getHeigth())
                .withAudioChannels(1)
                .withAudioSamplerate(48000)
                .withAudioBitrate(192 * 1000)
                .build(); */

        mConfig = new SessionConfig.Builder(outputLocation)
                .withVideoBitrate(5000000)
                .withVideoResolution(1280, 720)
                .withAudioChannels(1)
                .withAudioSamplerate(48000)
                .withAudioBitrate(192 * 1000)
                .build();

        listener.onInitSession(mConfig);

    }

    /**
     * Get absolute output video path
     * @return
     */
    public String getOutputVideoPath(){
        return outputLocation;
    }

    /**
     * Start recording use case.
     * //TODO Do something.
     * @param listener
     */
    public void startRecording(OnRecordEventListener listener){


        listener.onRecordStarted();
    }

    /**
     * Stop recording use case.
     * //TODO Do something.
     * @param listener
     */
    public void stopRecording(OnRecordEventListener listener){

        listener.onRecordStopped();
    }

    /**
     * Get available camera effects fx
     *
     * @param listener
     */
    public void getAvailableCameraEffectFx(OnCameraEffectFxListener listener) {
        /// TODO getAvailableColorEffects from model
        List<CameraEffectFxList> cameraEffectFxList = CameraEffectFxList.getCameraEffectList();
        //ArrayList<String> effectList = ColorEffectList.getColorEffectList(getCameraInstance());
        listener.onCameraEffectFxListRetrieved(cameraEffectFxList);
    }

    /**
     * Get available camera effects color
     *
     * @param listener
     */
    public void getAvailableCameraEffectColor(OnCameraEffectColorListener listener, Camera camera) {
        /// TODO getAvailableColorEffects from model

           // List<CameraEffectColorList> cameraEffectColorList = CameraEffectColorList.getCameraEffectColorListSorted(camera);
            List<CameraEffectColorList> cameraEffectColorList = CameraEffectColorList.getCameraEffectColorList();

            cameraEffectColorListAux = cameraEffectColorList;

            listener.onCameraEffectColorListRetrieved(cameraEffectColorList);
    }


    /**
     * Add Android camera color effect
     * //TODO use openGL color effect instead of camera color effect
     *
     * @param position
     */
        //TODO add CameraEffect, add Effect, add time and add effect to Project
    public void addAndroidCameraEffect(int position, Camera camera) {

        String colorEffect =  cameraEffectColorListAux.get(position).getNameResourceId();

        Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(colorEffect);
        camera.setParameters(parameters);

        //TODO, make listener and register event, send tracking
        //listener.onColorEffectAdded(colorEffect, getTimeColorEffect());

    }


    /**
     * Remove effect
     */
    //TODO removeEffect, add time and remove effect from Project
  /*  public void removeEffect(String colorEffect, OnColorEffectListener listener) {
        // removeEffect, addEffect none. Implement effect.getDefaultName()
        Camera.Parameters parameters = camera.getParameters();
        // parameters.setColorEffect(effect.getDefaultName());
        parameters.setColorEffect(Constants.COLOR_EFFECT_NONE);
        camera.setParameters(parameters);
        listener.onColorEffectRemoved(colorEffect, timer.getBase());
    }
   */

    /**
     * Se timer, to register color effect duration
     */
    private void setTimer() {
        timeColorEffect = SystemClock.uptimeMillis();
    }

    /**
     * Get time color effect applied
     * @return
     */
    private long getTimeColorEffect() {
        if (timeColorEffect == 0) {
            return 0;
        } else {
            return SystemClock.uptimeMillis() - timeColorEffect;
        }
    }

    /**
     * Get camera settings
     */
    public void getSettingsCamera(OnSettingsCameraListener listener, Camera camera) {
        listener.onSettingsCameraSuccess(supportChangeCamera(camera), supportFlashMode(camera));
    }


    /**
     * Support flash mode
     * //TODO check flash support from model, now check from SharePreferences, private data.
     */
    private boolean supportFlashMode(Camera camera) {

        if(camera.getParameters().getSupportedFlashModes() != null) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Support autofocus mode
     */
    private boolean supportAutoFocus(Camera camera){
        List<String> focusModes = camera.getParameters().getSupportedFocusModes();
        /* Focus continuous doesn't work well, we use autoFocus onTouchEvent
        if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parms.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else */
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Support change camera. Minimum quality 720p
     * //TODO, study force 720p record to front camera, minimum quality
     */
    private boolean supportChangeCamera(Camera camera) {

        Log.d(LOG_TAG, "camera getNumberOfCameras " + camera.getNumberOfCameras());

        if(camera.getNumberOfCameras() > 1) {

            List<Camera.Size> supportedVideoSizes;
            supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
            if (supportedVideoSizes != null) {
                for (Camera.Size size : camera.getParameters().getSupportedVideoSizes()) {
                    if (size.width == 1280 && size.height == 720) {
                        return true;
                    }
                    if (size.width == 1920 && size.height == 1080) {
                        return true;
                    }
                    if (size.width == 3840 && size.height == 2160) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * Add flash mode torch
     */
    public void addFlashMode(Camera camera, OnFlashModeListener listener) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        listener.onFlashModeTorchAdded();
    }

    /**
     * Remove flash mode torch
     */
    public void removeFlashMode(Camera camera, OnFlashModeListener listener) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        listener.onFlashModeTorchRemoved();
    }


}
