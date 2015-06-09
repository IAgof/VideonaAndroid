/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.widget.Chronometer;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnChangeCameraListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnColorEffectListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnFlashModeListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnPreviewListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRecordEventListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnSettingsCameraListener;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectList;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
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
     * CustomManualFocusView
     */
    private CustomManualFocusView customManualFocusView;

    /**
     * Camera Android
     */
    private Camera camera;

    /**
     * Int cameraId. default back camera
     */
    private int cameraId = 0;

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

    /**
     * Time to select color effect
     */
    private long timeColorEffect = 0;

    /**
     *  Rotation View
     */
    private int rotationView = 0;

    /**
     * User preferences, private data
     */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Context
     */
    private Context context;

    /**
     * Boolean supportAutofocus
     */
    private boolean autoFocus = true;

    public RecordUseCase(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(camera == null){
            camera = getCameraInstance(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID,ConfigPreferences.BACK_CAMERA));
        }
       // this.rotationView = rotationView;
        cameraPreview = new CameraPreview(context, camera);
        customManualFocusView = new CustomManualFocusView(context);
        timer = new Chronometer(context);
        Log.d(LOG_TAG, "RecordUseCase");
    }

    /**
     * Start preview camera
     *
     * @param listener
     */
    public void startPreview(OnPreviewListener listener, int displayOrientation){
        listener.onPreviewStarted(cameraPreview, customManualFocusView, autoFocus);
        setCameraOrientation(displayOrientation);
    }


    /**
     * ReStart preview camera
     *
     * @param listener
     */
    public void reStartPreview(OnPreviewListener listener){
        listener.onPreviewReStarted(cameraPreview, customManualFocusView, autoFocus);
    }

    /**
     * Start to record video file
     *
     * @param listener
     */
    public void startRecord(OnRecordEventListener listener){
        if (prepareVideoRecorder(camera, cameraPreview)) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mediaRecorder.start();
            setTimer();
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
    public void stopRecord(OnRecordEventListener listener) {
        Log.d(LOG_TAG, "timer " + (SystemClock.uptimeMillis() - timeColorEffect));
        // Disable stop record during 2 second
        if((SystemClock.uptimeMillis() - timeColorEffect) < 2000) {
            //Do nothing
        } else {
            mediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(camera); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder
            //releaseCamera(camera, cameraPreview);
            timer.stop();
            // inform the user that recording has stopped
            listener.onRecordStopped();
        }
    }

    public void stopCamera(){
            releaseCamera(camera, cameraPreview);
    }

    public void stopMediaRecorder(OnRecordEventListener listener){
        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(camera); // release the MediaRecorder object
        camera.lock();         // take camera access back from MediaRecorder
        releaseCamera(camera, cameraPreview);
        timer.stop();
        listener.onRecordRestarted();
    }

    /**
     * onResume UseCase, getCamera
     */
    public void onResume(){
        Log.d(LOG_TAG, "RecordUseCase onResume() ");

        if(camera == null) {
            camera = getCameraInstance(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID,
                    ConfigPreferences.BACK_CAMERA));
        }
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
    public void getAvailableEffects(OnColorEffectListener listener){
        /// TODO getAvailableEffects from model
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
    public void addEffect(String colorEffect, OnColorEffectListener listener){
        Camera.Parameters parameters = camera.getParameters();
        parameters.setColorEffect(colorEffect);
        //camera.setDisplayOrientation(180);
        camera.setParameters(parameters);
        listener.onColorEffectAdded(colorEffect, getTimeColorEffect());
        Log.d(LOG_TAG, " addEffect " + colorEffect + " time " + getTimeColorEffect());
    }

    /**
     * Remove effect
     *
     * @param colorEffect
     * @param listener
     */
    public void removeEffect(String colorEffect, OnColorEffectListener listener){
       // removeEffect, addEffect none. Implement effect.getDefaultName()
        Camera.Parameters parameters = camera.getParameters();
        // parameters.setEffect(effect.getDefaultName());
        parameters.setColorEffect(Constants.COLOR_EFFECT_NONE);
        camera.setParameters(parameters);
        listener.onColorEffectRemoved(colorEffect, timer.getBase());
    }

    /**
     * Get camera settings
     */
    public void getSettingsCamera(OnSettingsCameraListener listener){
        listener.onSettingsCameraSuccess(supportChangeCamera(), supportFlashMode());
    }

    /**
     * Support change camera. Minimum quality 720p
     */
    private boolean supportChangeCamera(){
        if(sharedPreferences.getBoolean(ConfigPreferences.FRONT_CAMERA_SUPPORTED, false)) {
            return true;
        }
        return false;
    }

    /**
     * Change camera
     */
    public void changeCamera(OnChangeCameraListener listener){
        int cameraMode = sharedPreferences.getInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA);
        int newCameraMode = cameraMode;
        switch (cameraMode){
            case ConfigPreferences.BACK_CAMERA:
                editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.FRONT_CAMERA).commit();
                newCameraMode = ConfigPreferences.FRONT_CAMERA;
                break;
            case ConfigPreferences.FRONT_CAMERA:
                editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA).commit();
                newCameraMode = ConfigPreferences.BACK_CAMERA;
                break;
            default:
                editor.putInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA).commit();
                newCameraMode = ConfigPreferences.BACK_CAMERA;
                listener.onChangeCameraError();
                break;
        }
        //Update cameraId
        cameraId = newCameraMode;

        if(cameraMode != newCameraMode) {
            // release camera, needed to open new cameraMode
            releasePreviewCameraChanged(camera, cameraPreview);
            // listener to removePreview, needed to open nee CameraPreview
            listener.onReleaseChangeCameraPreview(cameraPreview, customManualFocusView,
                    autoFocus);
            // Update new camera, cameraPreview, customManualFocuscView
            // listener succes, change icon resource
            listener.onChangeCameraSuccess(newCameraMode);
        } else {
            listener.onChangeCameraError();
        }
    }

    /**
     * Support flash mode
     * //TODO check flash support from model, now check from SharePreferences, private data.
     *
     */
    private boolean supportFlashMode(){
        cameraId = sharedPreferences.getInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA);
        boolean isFlashSupported;
        switch (cameraId){
            case ConfigPreferences.BACK_CAMERA:
                isFlashSupported = sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, false);
                break;
            case ConfigPreferences.FRONT_CAMERA:
                isFlashSupported = sharedPreferences.getBoolean(ConfigPreferences.FRONT_CAMERA_FLASH_SUPPORTED, false);
                break;
            default:
                isFlashSupported = sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_FLASH_SUPPORTED, false);
                break;
        }
        return isFlashSupported;
    }

    /**
     * Add flash mode torch
     */
    public void addFlashMode(String flashMode, OnFlashModeListener listener){
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(flashMode);
        camera.setParameters(parameters);
        listener.onFlashModeTorchAdded();
    }

    /**
     * Remove flash mode torch
     *
     */
    public void removeFlashMode(String flashMode, OnFlashModeListener listener){
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(flashMode);
        camera.setParameters(parameters);
        listener.onFlashModeTorchRemoved();
    }

    /*
    * A safe way to get an instance of the Camera object.
    */
    public Camera getCameraInstance(int cameraId) {
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
           // autoFocus = supportAutoFocus(c);
            //  }
        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
            // Camera is not available (in use or does not exist)
        }
        Log.d(LOG_TAG, " getCameraInstance camera " + c);
        return c; // returns null if camera is unavailable
    }

    /**
     *  Change camera, release camera and preview
     */
    private void releasePreviewCameraChanged(Camera camera, CameraPreview cameraPreview){
        releaseCamera(camera, cameraPreview);
    }

    private boolean supportAutoFocus(Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        for(String autoFocus: parameters.getSupportedFocusModes()){
            if(autoFocus.compareTo(Camera.Parameters.FOCUS_MODE_AUTO) == 0){
                Log.d(LOG_TAG, "Autofocus supported");
                return true;
            }
        }
        Log.d(LOG_TAG, "Autofocus not supported");
        return false;
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
        setVideoRecordName(videoRecordName);
        // rotateVideo according to preview
        rotateVideo(getRotationView(), mediaRecorder);
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
     *  Set camera displayOrientation
     * @param displayOrientation
     */
    private void setCameraOrientation(int displayOrientation){
        camera.setDisplayOrientation(displayOrientation);
    }

    /**
     *
     * @param rotationPreview mMediaRecorder
     * @return
     */
    private void rotateVideo(int rotationPreview, MediaRecorder mediaRecorder) {
        /**
         * Define Orientation of video in here,
         * if in portrait mode, use value = 90,
         * if in landscape mode, use value = 0
         */

        int cameraOrientation = getCameraDisplayOrientation(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA));

        Log.d(LOG_TAG, "rotationPreview MediaRecorder rotation Preview " + rotationPreview);

        switch (rotationPreview) {
            case 1:
                if(cameraOrientation == 90) {
                    mediaRecorder.setOrientationHint(0);
                }
                if(cameraOrientation == 270){
                    mediaRecorder.setOrientationHint(180);
                }
                break;
            case 3:
                if(cameraOrientation == 90) {
                    mediaRecorder.setOrientationHint(180);
                }
                if(cameraOrientation == 270){
                    mediaRecorder.setOrientationHint(0);
                }
                break;
            default:
                if(cameraOrientation == 90) {
                    mediaRecorder.setOrientationHint(0);
                }
                if(cameraOrientation == 270){
                    mediaRecorder.setOrientationHint(180);
                }
                break;
        }
    }

    /**
     *  Release MediaRecorder
     */
    private void releaseMediaRecorder(Camera camera) {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   //clear recorder configuration
            mediaRecorder.release(); //release the recorder object
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

    private void setTimer() {
        timeColorEffect = SystemClock.uptimeMillis();
    }

    private long getTimeColorEffect(){
        if(timeColorEffect == 0) {
            return 0;
        } else {
            return SystemClock.uptimeMillis() - timeColorEffect;
        }
    }

    //TODO To delete. Temporal, necessary to navigate to EditActivity
    public String getVideoRecordName(){
        return videoRecordName;
    }

    private void setVideoRecordName(String videoRecordName){
        this.videoRecordName = videoRecordName;
    }

    private int getRotationView() {
        return rotationView;
    }

    public void setRotationView(int rotationView){
        this.rotationView = rotationView;
        int cameraOrientation = getCameraDisplayOrientation(sharedPreferences.getInt(ConfigPreferences.CAMERA_ID, ConfigPreferences.BACK_CAMERA));
        int displayOrientation = 0;
        if(rotationView == Surface.ROTATION_90){
            if(cameraOrientation == 90) {
                displayOrientation = 0;
            }
            if(cameraOrientation == 270){
                displayOrientation = 180;
            }
            Log.d(LOG_TAG, "setRotationView rotation 90, cameraOrientation " + cameraOrientation );
        }
        if(rotationView == Surface.ROTATION_270){
            if(cameraOrientation == 90) {
                displayOrientation = 180;
            }
            if(cameraOrientation == 270){
                displayOrientation = 0;
            }
            Log.d(LOG_TAG, "setRotationView rotation 270, cameraOrientation " + cameraOrientation );
        }
        cameraPreview.setCameraOrientation(displayOrientation);
    }
    
    // setDisplayOrientation, fix Iago camera settings
   // public static void setCameraDisplayOrientation(Activity activity,
     //                                              int cameraId, android.hardware.Camera camera) {
    public int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        //int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
       /* int rotation = displayOrientation;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        */
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation ) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation + 360) % 360;
        }
       // camera.setDisplayOrientation(result);
        Log.d(LOG_TAG, "getCameraDisplayOrientation cameraId " + cameraId + " result " + result);
        return result;
    }
}
