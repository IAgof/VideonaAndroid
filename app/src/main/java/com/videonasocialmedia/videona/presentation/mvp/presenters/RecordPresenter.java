/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;


import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;

import com.videonasocialmedia.videona.domain.record.GetVideoRecordedUseCase;
import com.videonasocialmedia.videona.domain.record.GetVideoRecordedUseCaseController;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectList;
import com.videonasocialmedia.videona.utils.ConfigUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordPresenter extends Presenter {

    GetVideoRecordedUseCase recordUseCase;

    private final RecordView recordView;

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * ArrayList colorFilter
     */
    public static ArrayList<String> colorFilter;

    /**
     * MediaRecorder
     */
    private MediaRecorder mediaRecorder;

    /**
     * Boolean, control is recording file
     */
    private boolean isRecording = false;

    /**
     * String absolute path video record file
     */
    private String videoRecordString;

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

    public RecordPresenter(RecordView recordView) {

        recordUseCase = new GetVideoRecordedUseCaseController();
        this.recordView = recordView;

    }

    /**
     * Called when the presenter is initialized
     */
    @Override
    public void start() {

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(recordView.getContext(), camera);

        recordView.startPreview(camera, cameraPreview);

        recordView.setChronometer();

        colorFilter = ColorEffectList.getColorEffectList(camera);

       // recordUseCase.startRecordFile();
    }

    /**
     * Called when the presenter is stop, i.e when an activity
     * or a fragment finishes
     */
    @Override
    public void stop() {

        recordUseCase.stopRecordFile();

    }


    public void onResume(){

        camera = getCameraInstance();

     //   recordView.startPreview(camera, cameraPreview);

    }

    public void onPause(){

        releaseMediaRecorder(camera);       // if you are using MediaRecorder, release it first
        releaseCamera(camera, cameraPreview);
    }

    /**
     * Called when the activity need a path to save record file data.
     *
     * @return String recordFile
     */
    public String getRecordFileString() {

        return recordUseCase.getRecordFileString();

    }

    /**
     * Called when the user stop to record, save record file duration
     *
     * @param recordFileDuration
     */
    public void setRecordFileDuration(long recordFileDuration){

        recordUseCase.setRecordFileDuration(recordFileDuration);
    }

    /**
     * Called when the user stop to record, save record file duration
     *
     * @param colorEffect
     */
    public void setColorEffect(String colorEffect, long time){

        recordUseCase.setColorEffect(colorEffect, time);

    }


    public void recordClickListener() {

        if (isRecording) {

            stopRecordVideo();

        } else {

            // Inicializar TrackObject, Media Track ...

            startRecordVideo();

        }

    }

    private void startRecordVideo() {

        if (prepareVideoRecorder(camera, cameraPreview)) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording

            recordView.startRecordVideo();

            isRecording = true;

            //initialize chronometerRecord
            recordView.startChronometer();

            mediaRecorder.start();


        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder(camera);
            // inform user
        }
    }

    private void stopRecordVideo(){

        recordView.stopRecordVideo();
        recordView.stopChronometer();

        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(camera); // release the MediaRecorder object
        camera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        //   buttonRecord.setImageResource(R.drawable.ic_btn_stop);  //setText("Capture");
        isRecording = false;

        releaseCamera(camera, cameraPreview);

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

        mediaRecorder.setVideoSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);
        mediaRecorder.setVideoFrameRate(ConfigUtils.VIDEO_FRAME_RATE);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(ConfigUtils.VIDEO_ENCODING_BIT_RATE);

        // Set output file
        videoRecordString = getRecordFileString();

        // Check if videoRecordString exists
        File f = new File(videoRecordString);
        if (f.exists()) {
            videoRecordString = videoRecordString + "1";
        }

        mediaRecorder.setOutputFile(videoRecordString);

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
            isRecording = false;
            camera.stopPreview();
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();        // release the camera for other applications
            camera = null;

        }
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

            ///TODO Study bestSetPictureSize
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
}
