/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;

import android.hardware.Camera;
import android.os.SystemClock;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnCameraEffectListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnColorEffectListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnFlashModeListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnSettingsCameraListener;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectList;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectList;

import java.util.ArrayList;
import java.util.List;

public class RecordUseCase {

    /**
     * Time to select color effect
     */
    private long timeColorEffect = 0;

    public RecordUseCase(){


    }


    /**
     * Get available camera effects
     *
     * @param listener
     */
    public void getAvailableCameraEffects(OnCameraEffectListener listener) {
        /// TODO getAvailableColorEffects from model
        ArrayList<String> cameraEffectList = CameraEffectList.getCameraEffectList();
        //ArrayList<String> effectList = ColorEffectList.getColorEffectList(getCameraInstance());
        listener.onCameraEffectListRetrieved(cameraEffectList);
    }

    public void addCameraEffect(String cameraEffect, OnCameraEffectListener listener) {

        listener.onCameraEffectAdded(cameraEffect, getTimeColorEffect());

    }


    /**
     * Get available color effects
     *
     * @param listener
     */
    public void getAvailableColorEffects(OnColorEffectListener listener, Camera camera) {
        /// TODO getAvailableColorEffects from model
        ArrayList<String> effectList = ColorEffectList.getColorEffectList(camera);
        //ArrayList<String> effectList = ColorEffectList.getColorEffectList(getCameraInstance());
        listener.onColorEffectListRetrieved(effectList);
    }

    /**
     * Add effect
     *
     * @param colorEffect
     * @param listener
     */
        //TODO add CameraEffect, add Effect, add time and add effect to Project
    public void addAndroidCameraEffect(String colorEffect, Camera camera, OnColorEffectListener listener) {
      /*  Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(colorEffect);
        //camera.setDisplayOrientation(180);
        camera.setParameters(parameters);
        listener.onColorEffectAdded(colorEffect, getTimeColorEffect());
        Log.d(LOG_TAG, " addAndroidCameraEffect " + colorEffect + " time " + getTimeColorEffect());
       */

        Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(colorEffect);
        camera.setParameters(parameters);
        listener.onColorEffectAdded(colorEffect, getTimeColorEffect());

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

    private void setTimer() {
        timeColorEffect = SystemClock.uptimeMillis();
    }

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
     */
    private boolean supportChangeCamera(Camera camera) {

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
