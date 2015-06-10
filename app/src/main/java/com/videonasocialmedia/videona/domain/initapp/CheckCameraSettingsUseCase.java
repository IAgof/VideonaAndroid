/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.initapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.videonasocialmedia.videona.utils.UserPreferences;

public class CheckCameraSettingsUseCase {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Context of application
     */
    private Context context;

    /**
     * User preferences, private data
     */
    UserPreferences userPreferences;

    /**
     * Camera Android
     */
    private Camera camera;

    /**
     * Camera id
     */
    private int cameraId = 0;


    public CheckCameraSettingsUseCase(Context context){

        this.context = context;

        userPreferences = new UserPreferences(context);

        //start back camera
        userPreferences.setCameraId(0);

    }

    /*
    * A safe way to get an instance of the Camera object.
    */
    public Camera getCameraInstance(int cameraId) {

        Camera c = null;

        try {

            c = Camera.open(cameraId); // attempt to get a Camera instance

        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
            // Camera is not available (in use or does not exist)
        }

        Log.d(LOG_TAG, " getCameraInstance camera " + c);

        return c; // returns null if camera is unavailable

    }

    public void checkNumCameras(){

        if(camera == null){
            camera = getCameraInstance(cameraId);
        }

        int numCameras = camera.getNumberOfCameras();

        if(numCameras > 1) {

            userPreferences.setFrontCameraSupported(true);

        }

        camera.release();

    }


    public void checkFlashMode(){

        if(camera == null){
            camera = getCameraInstance(cameraId);
        }

        int numCameras = camera.getNumberOfCameras();

        // Check front camera
        if(numCameras > 1) {
            // Support FrontCamera
            if(camera != null){

                camera.release();
                cameraId = 1;
                camera = getCameraInstance(cameraId);

                if(camera.getParameters().getSupportedFlashModes() != null){
                    // Camera front support flashMode
                    // Save to model, list of supportedFlashMode
                    // TODO support flash mode not only Torch
                    userPreferences.setFrontCameraFlashSupported(true);

                } else {
                    userPreferences.setFrontCameraFlashSupported(false);
                }
            }

        }

        // Check back camera
        if(camera != null){

            camera.release();
            cameraId = 0;
            camera = getCameraInstance(cameraId);

            if(camera.getParameters().getSupportedFlashModes() != null){
                // Camera back support flashMode
                // Save to model, list of supportedFlashMode
                // TODO support flash mode not only Torch
                userPreferences.setBackCameraFlashSupported(true);

            } else {
                userPreferences.setBackCameraFlashSupported(false);
            }
        }

        camera.release();

    }


    public void checkCameraVideoSize(){

        if(camera == null){
            camera = getCameraInstance(cameraId);
        }

        int numCameras = camera.getNumberOfCameras();

        // Check front camera
        if(numCameras > 1) {
            // Support FrontCamera
            if(camera != null){

                camera.release();
                cameraId = 1;
                camera = getCameraInstance(cameraId);

                // Check 720p, 1080p, 2160p
                for(Camera.Size size: camera.getParameters().getSupportedVideoSizes()){

                    Log.d(LOG_TAG, "checkCameraVideoSize frontCamera " + size.width + "x" + size.height);

                    if(size.width == 1280 && size.height == 720){
                        userPreferences.setFrontCamera720pSupported(true);
                    }
                    if(size.width == 1920 && size.height == 1080){
                        userPreferences.setFrontCamera1080pSupported(true);
                    }

                }

            }

        }

        // Check back camera
        if(camera != null){

            camera.release();
            cameraId = 0;
            camera = getCameraInstance(cameraId);

            // Check 720p, 1080p, 2160p
            for(Camera.Size size: camera.getParameters().getSupportedVideoSizes()){

                Log.d(LOG_TAG, "checkCameraVideoSize backCamera " + size.width + "x" + size.height);

                if(size.width == 1280 && size.height == 720){
                    userPreferences.setBackCamera720pSupported(true);
                }
                if(size.width == 1920 && size.height == 1080){
                    userPreferences.setBackCamera1080pSupported(true);
                }
                if(size.width == 3840 && size.height == 2160){
                    userPreferences.setBackCamera2160pSupported(true);
                }

            }
        }

        camera.release();

    }

}
