/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.widget.Chronometer;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.onEffectListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.onPreviewListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.onRecordEventListener;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectList;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordUseCase {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * MediaRecorder
     */
    private MediaRecorder mediaRecorder;

    /**
     * CameraPreview
     */
    private CameraPreview cameraPreview;

    /**
     * Int cameraId. Future use to multicamera
     */
    private int cameraId = 0;

    /**
     * Camera Android
     */
    private Camera camera;

    /**
     * Media type video
     */
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * String video record name
     */
    private String videoRecordName;

    /**
     * Timer to save effects time
     */
    private Chronometer timer;

    public RecordUseCase(Context context){

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(context, camera);

        timer = new Chronometer(context);


    }

    /**
     * Start preview camera
     *
     * @param listener
     */
    public void startPreview(onPreviewListener listener){

        listener.onPreviewStarted(camera, cameraPreview);

    }

    /**
     * Start to record video file
     *
     * @param listener
     */
    public void startRecord(onRecordEventListener listener){


        if (prepareVideoRecorder(camera, cameraPreview)) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording

            mediaRecorder.start();

            timer.start();

            listener.onRecordStarted();

        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder(camera);
            // inform user
            listener.onRecordStopped();
        }
    }

    /**
     * Stop to record video file
     *
     * @param listener
     */
    public void stopRecord(onRecordEventListener listener) {

        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(camera); // release the MediaRecorder object
        camera.lock();         // take camera access back from MediaRecorder

        releaseCamera(camera, cameraPreview);

        timer.stop();
        // inform the user that recording has stopped
        listener.onRecordStopped();

    }

    /**
     * onResume UseCase, getCamera
     */
    public void onResume(){

        camera = getCameraInstance();

        //   recordView.startPreview(camera, cameraPreview);

    }

    /**
     * onPause UseCase, release camera and mediaRecorder
     */
    public void onPause(){

        releaseMediaRecorder(camera);       // if you are using MediaRecorder, release it first
        releaseCamera(camera, cameraPreview);
    }

    /**
     * Get available effects from model
     *
     * @param listener
     */
    public void getAvailableEffects(onEffectListener listener){


        /// TODO getAvailableEffects from model
        ArrayList<String> effectList = ColorEffectList.getColorEffectList(camera);

        listener.onEffectListRetrieved(effectList);

    }

    /**
     * Add effect
     *
     * @param effect
     * @param listener
     */
    public void addEffect(Effect effect, onEffectListener listener){


        Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(effect.getName());
        camera.setParameters(parameters);

        listener.onEffectAdded(effect, timer.getBase());

    }

    /**
     * Remove effect
     *
     * @param effect
     * @param listener
     */
    public void removeEffect(Effect effect, onEffectListener listener){

       // removeEffect, addEffect none. Implement effect.getDefaultName()

        Camera.Parameters parameters = camera.getParameters();
        // parameters.setEffect(effect.getDefaultName());
        parameters.setColorEffect(Constants.COLOR_EFFECT_NONE);
        camera.setParameters(parameters);

        listener.onEffectRemoved(effect, timer.getBase());

    }

    /*
    * A safe way to get an instance of the Camera object.
    */
    public Camera getCameraInstance() {

        Camera c = null;
        try {

            c = Camera.open(cameraId); // attempt to get a Camera instance

            // if (c != null) {
            Camera.Parameters params = c.getParameters();

            ///TODO get VideoSize from model, Project Profile
            params.setPictureSize(ConfigUtils.VIDEO_SIZE_HEIGHT, ConfigUtils.VIDEO_SIZE_WIDTH);
            c.setParameters(params);

            Log.d(LOG_TAG, "getCameraInstance height " + c.getParameters().getPictureSize().height + " width " + c.getParameters().getPictureSize().width);

            // SetFrameRate
            params.setPreviewFrameRate(30);
            params.setPreviewFpsRange(30000, 30000);


            // Log CameraParameters info
            Log.d(LOG_TAG, " getParameters().getSupportedPreviewFrameRates() " );
            for(int framerate: c.getParameters().getSupportedPreviewFrameRates()){
                Log.d(LOG_TAG, " framerate: " + framerate);
            }

            Log.d(LOG_TAG, " getParameters(). getSupportedPreviewFpsRange ()() " );
            for(int[] fpsrange: c.getParameters().getSupportedPreviewFpsRange()){
                Log.d(LOG_TAG, " fpsrange: " + fpsrange[0] + " x " + fpsrange[1]);
            }



            //  }

        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
            // Camera is not available (in use or does not exist)
        }

        Log.d(LOG_TAG, " getCameraInstance camera " + c);



        return c; // returns null if camera is unavailable
    }


    /**
     * Prepare VideoRecorder.
     *
     * Set Audio and Video Settings, ConfigUtils
     *
     * Use Videona Profile to record Video
     *
     * @return boolean isPrepared
     */
    private boolean prepareVideoRecorder(Camera camera, CameraPreview cameraPreview) {

        mediaRecorder = new MediaRecorder();

        // Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        // Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //  mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // Audio
        mediaRecorder.setAudioSamplingRate(ConfigUtils.AUDIO_SAMPLING_RATE);
        mediaRecorder.setAudioChannels(ConfigUtils.AUDIO_CHANNELS);
        mediaRecorder.setAudioEncodingBitRate(ConfigUtils.AUDIO_ENCODING_BIT_RATE);

        // Video

        ///TODO get VideoSize from model, Project Profile
        mediaRecorder.setVideoSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);
        mediaRecorder.setVideoFrameRate(ConfigUtils.VIDEO_FRAME_RATE);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(ConfigUtils.VIDEO_ENCODING_BIT_RATE);

        // Set output file
        videoRecordName = getOutputRecordFile(MEDIA_TYPE_VIDEO).toString();

        // Check if videoRecordName exists
        File f = new File(videoRecordName);
        if (f.exists()) {
            videoRecordName = videoRecordName + "1";
        }

        mediaRecorder.setOutputFile(videoRecordName);

        // Set the frameLayoutCameraPreview output
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        // Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("DEBUG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder(camera);
            return false;
        } catch (IOException e) {
            Log.d("DEBUG", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder(camera);
            return false;
        }
        return true;
    }

    /**
     *  Release MediaRecorder
     */
    private void releaseMediaRecorder(Camera camera) {

        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.setPreviewCallback(null);
            camera.lock();           // lock camera for later use
        }
    }

    /**
     *  Release Camera
     */
    private void releaseCamera(Camera camera, CameraPreview cameraPreview) {

        if (camera != null) {

            camera.stopPreview();
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();        // release the camera for other applications
            camera = null;

        }
    }



    /**
     * Create a file Uri for saving video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputRecordFile(type));
    }

    private static File getOutputRecordFile(int type) {

        File mediaStorageDir = new File(Constants.PATH_APP_MASTERS);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                Log.d("GetVideoRecordedUseCaseController", "failed to create directory");
                return null;

            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


}
