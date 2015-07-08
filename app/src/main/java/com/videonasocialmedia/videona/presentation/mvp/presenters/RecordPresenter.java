/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.videonasocialmedia.videona.avrecorder.AndroidEncoder;
import com.videonasocialmedia.videona.avrecorder.CameraEncoder;
import com.videonasocialmedia.videona.avrecorder.MicrophoneEncoder;
import com.videonasocialmedia.videona.avrecorder.SessionConfig;
import com.videonasocialmedia.videona.avrecorder.gles.FullFrameRect;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.record.RecordUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.RecordView;
import com.videonasocialmedia.videona.presentation.views.GLCameraView;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordPresenter implements OnCameraEffectListener, OnColorEffectListener,
        OnFlashModeListener, OnSettingsCameraListener, OnRemoveMediaFinishedListener,
        OnAddMediaFinishedListener, OnSessionConfigListener,
        AndroidEncoder.OnMuxerFinishedEventListener, OnRecordEventListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter"; //getClass().getSimpleName();

    private final RecordView recordView;
    private final RecordUseCase recordUseCase;

    private SessionConfig mConfig;

    /**
     * Add Media to Project Use Case
     */
    AddVideoToProjectUseCase addVideoToProjectUseCase;

    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;

    protected CameraEncoder mCamEncoder;

    protected MicrophoneEncoder mMicEncoder;

    private boolean mIsRecording;

    private boolean isEncoderVideoFinished = false;

    private boolean isEncoderAudioFinished = false;

    /**
     * Boolean, control flash mode, pressed or not
     */
    private boolean isFlahModeON = false;

    // Message AVRecorderHandler
    public static final int MSG_AVRECORDER_FINISH = 100;

    // Handler to control Android Muxer is finished, record file created correctly, audio and video
    private AVRecorderHandler mHandler = new AVRecorderHandler(this);


    public RecordPresenter(final RecordView recordView) throws IOException {

        this.recordView = recordView;
        recordUseCase = new RecordUseCase();

        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();

        recordUseCase.initSessionConfig(this);

    }

    private void init(SessionConfig config) throws IOException {
        mCamEncoder = new CameraEncoder(config, this);
        mMicEncoder = new MicrophoneEncoder(config, this);
        mIsRecording = false;
        mConfig = config;


    }


    public SessionConfig getSessionConfig() {
        return mConfig;
    }

    public void setPreviewDisplay(GLCameraView display) {
        mCamEncoder.setPreviewDisplay(display);
    }

    public void applyFilter(int filter){
        mCamEncoder.applyFilter(filter);
    }

    public void requestOtherCamera(){
        mCamEncoder.requestOtherCamera();

    }

    public void requestCamera(int camera){
        mCamEncoder.requestCamera(camera);
    }

    public void toggleFlash(){
        mCamEncoder.toggleFlashMode();
    }

    public void adjustVideoBitrate(int targetBitRate) {
        mCamEncoder.adjustBitrate(targetBitRate);
    }

    /**
     * Signal that the recorder should treat
     * incoming video frames as Vertical Video, rotating
     * and cropping them for proper display.
     *
     * This method only has effect if {SessionConfig#setConvertVerticalVideo(boolean)}
     * has been set true for the current recording session.
     *
     */
    public void signalVerticalVideo(FullFrameRect.SCREEN_ROTATION orientation) {
        mCamEncoder.signalVerticalVideo(orientation);
    }

    public void startRecording(){
        mIsRecording = true;
        mMicEncoder.startRecording();
        mCamEncoder.startRecording();

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File outputLocation = new File(Constants.PATH_APP_MASTERS, fileName);
        mConfig.setOutputDirectory(outputLocation);

        recordUseCase.startRecording(this);

    }

    public boolean isRecording(){
        return mIsRecording;
    }

    public void stopRecording(){
        mIsRecording = false;
        mMicEncoder.stopRecording();
        mCamEncoder.stopRecording();

        recordUseCase.stopRecording(this);


    }

    // TODO study recording pause functionality
    public void pauseRecording(){

    }

    private void clearProject() {
        Project project=Project.getInstance(null, null, null);
        project.setMediaTrack(new MediaTrack());
        removeMusicFromProjectUseCase.removeAllMusic(0, this);
    }



    /**
     * Prepare for a subsequent recording. Must be called after {@link #stopRecording()}
     * and before {@link #release()}
     * @param config
     */
    private void reset(SessionConfig config) throws IOException {
        mCamEncoder.reset(config);
        mMicEncoder.reset(config);
        mIsRecording = false;

    }

    /**
     * Release resources. Must be called after {@link #stopRecording()} After this call
     * this instance may no longer be used.
     */
    public void release() {

        if(mCamEncoder!= null) {
            mCamEncoder.release();
            Log.d(LOG_TAG,"release mCamEncoder");
        }
        // MicrophoneEncoder releases all it's resources when stopRecording is called
        // because it doesn't have any meaningful state
        // between recordings. It might someday if we decide to present
        // persistent audio volume meters etc.
        // Until then, we don't need to write MicrophoneEncoder.release()

        //Nexus4 release Mic! mMicEncoder.release();
        if(mMicEncoder!= null){
            mMicEncoder = null;
            Log.d(LOG_TAG,"release mMicEncoder");
        }

        //recordUseCase.finishSessionConfig();

        // Release mHandler
        mHandler.removeCallbacksAndMessages(null);

    }

    public void onHostActivityPaused(){
        mCamEncoder.onHostActivityPaused();
    }

    public void onHostActivityResumed(){
        mCamEncoder.onHostActivityResumed();
    }


    /**
     * Effect Button pressed
     */
    public void colorEffectClickListener() {
        recordUseCase.getAvailableColorEffects(this, mCamEncoder.getCamera());
    }

    /**
     * Settings Camera listener
     */
    public void settingsCameraListener() {

        recordUseCase.getSettingsCamera(this, mCamEncoder.getCamera());

    }

    /**
     * Effect Button pressed
     */
    public void cameraEffectClickListener() {
        recordUseCase.getAvailableCameraEffects(this);
    }

    /**
     * Flash Mode Torch pressed
     *
     * //TODO change to onFlashModeListener, support all kind of flash models.
     * // Get list of supportedFlashModel on SplashScreen and save to model.
     */
    public void onFlashModeTorchListener(){

        if(isFlahModeON){

            recordUseCase.removeFlashMode(mCamEncoder.getCamera(), this);


            isFlahModeON = false;

        } else {

            recordUseCase.addFlashMode(mCamEncoder.getCamera(), this);
            isFlahModeON = true;
        }

    }


        /**
         * Color effect selected
         *
         * @param effect
         */
    //TODO Add effect use case
    public void setColorEffect(String effect) {
        recordUseCase.addAndroidCameraEffect(effect, mCamEncoder.getCamera(), this);

    }

    /**
     * Camera Effect selected
     *
     * @param filter
     */
    //TODO Add effect use case
    public void setCameraEffect(int filter) {
        mCamEncoder.applyFilter(filter);
    }

    @Override
    public void onColorEffectAdded(String colorEffect, long time) {
       // sendButtonTracked(colorEffect, time);
        recordView.showEffectSelected(colorEffect);
        Log.d(LOG_TAG, "onColorEffectAdded");
    }

    @Override
    public void onColorEffectRemoved(String colorEffect, long time) {
        recordView.showEffectSelected(colorEffect);
        Log.d(LOG_TAG, "onColorEffectRemoved");
    }

    @Override
    public void onColorEffectListRetrieved(ArrayList<String> effects) {
        recordView.showEffects(effects);
        Log.d(LOG_TAG, "onColorEffectListRetrieved");
    }

    @Override
    public void onCameraEffectAdded(String cameraEffect, long time) {
        recordView.showCameraEffectSelected(cameraEffect);
      //  mCamEncoder.applyFilter(filter);

    }

    @Override
    public void onCameraEffectRemoved(String cameraEffect, long time) {
        recordView.showCameraEffectSelected(cameraEffect);

    }

    @Override
    public void onCameraEffectListRetrieved(ArrayList<String> effect) {
        recordView.showCameraEffects(effect);
    }

    @Override
    public void onFlashModeTorchAdded() {
        recordView.showFlashModeTorch(true);
        Log.d(LOG_TAG, "onFlashModeTorchAdded");
    }

    @Override
    public void onFlashModeTorchRemoved() {
        recordView.showFlashModeTorch(false);
        Log.d(LOG_TAG, "onFlashModeTorchRemoved");
    }

    @Override
    public void onFlashModeTorchError() {

    }

    @Override
    public void onSettingsCameraSuccess(boolean isChangeCameraSupported, boolean isFlashSupported) {

        recordView.showSettingsCamera(isChangeCameraSupported, isFlashSupported);
        Log.d(LOG_TAG, "onSettingsCameraSuccess isChangeCameraSupported " + isChangeCameraSupported
                + " isFlashSupported " + isFlashSupported);

    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {

    }

    @Override
    public void onAddMediaItemToTrackError() {

    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {

        recordView.navigateEditActivity();

    }


    @Override
    public void onInitSession(SessionConfig mConfig) {

        try {
            init(mConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRecordStarted() {

        recordView.showRecordStarted();
        recordView.lockScreenRotation();
        recordView.lockNavigator();
        recordView.startChronometer();

        Log.d(LOG_TAG, "onRecordStarted");
    }

    @Override
    public void onRecordStopped() {

        release();

        recordView.showRecordFinished();
        recordView.stopChronometer();
        recordView.unLockNavigator();

        Log.d(LOG_TAG, "onRecordStopped");

    }


    @Override
    public void onMuxerFinishedEventVideo() {

        Log.d(LOG_TAG, "onMuxerFinishedEventVideo");
        isEncoderVideoFinished = true;

        checkFinishEncoder();


    }

    @Override
    public void onMuxerFinishedEventAudio() {

        Log.d(LOG_TAG, "onMuxerFinishedEventAudio");

        isEncoderAudioFinished = true;

        checkFinishEncoder();
    }

    @Override
    public void onMuxerAudioError(){

        Log.d(LOG_TAG, "onMuxerAudioError");

      /*  if(isRecording()){
            stopRecording();
            mIsRecording = false;
        }
        */
        //release();

        recordView.reStartFragment();
    }

    @Override
    public void onMuxerVideoError(){

    }

    private boolean checkFinishEncoder(){

        if(isEncoderVideoFinished && isEncoderAudioFinished){

            mHandler.sendMessage(mHandler.obtainMessage(MSG_AVRECORDER_FINISH));

            return true;
        }
        return false;
    }

    public void setMsgAvrecorderFinish() {


        //renameFileRecorded();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File fRecord = new File(Constants.PATH_APP_MASTERS, fileName);

        File fTempRecord = new File((recordUseCase.getOutputVideoPath()));

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                Log.d("SHOW TIME TAG", "" );
                retriever.setDataSource(fTempRecord.getAbsolutePath());
                //String duration = retriever.extractMetadata(
                       // MediaMetadataRetriever.METADATA_KEY_DURATION);
                //int durationInt = Integer.parseInt(duration);

                fTempRecord.renameTo(fRecord);

               // clearProject();
                addVideoToProjectUseCase.addVideoToTrack(fRecord.getAbsolutePath(), this);


            } catch (Exception e) {

            }

    }

    static class AVRecorderHandler extends Handler {

        private WeakReference<RecordPresenter> mWeakAVRecorder;

        public AVRecorderHandler(RecordPresenter recordPresenter) {
            mWeakAVRecorder = new WeakReference<RecordPresenter>(recordPresenter);
        }

        @Override
        public void handleMessage(Message inputMessage) {

            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            RecordPresenter recordPresenter = mWeakAVRecorder.get();
            if (recordPresenter == null) {
                Log.w(LOG_TAG, "EncoderHandler.handleMessage: encoder is null");
                return;
            }

            switch (what) {
                case MSG_AVRECORDER_FINISH:
                    recordPresenter.setMsgAvrecorderFinish();
                    break;

                default:
                    throw new RuntimeException("Unexpected msg what=" + what);
            }
        }
    }

   /* private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation ) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation + 360) % 360;
        }

        Log.d(LOG_TAG, "setCameraDisplayOrientation cameraId " + cameraId + " result " + result);

        return result;
    }

    private void detectRotationView(Context context, int cameraId) {

        int rotationView = 0;
        rotationView = getWindowManager().getDefaultDisplay().getRotation();
        boolean detectScreenOrientation90 = false;
        boolean detectScreenOrientation270 = false;
        int displayOrientation = 0;

        int cameraOrientation = getCameraDisplayOrientation(cameraId);

        if (rotationView == Surface.ROTATION_90) {
            detectScreenOrientation90 = true;

            if (cameraOrientation == 90) {
                displayOrientation = 0;
            }
            if (cameraOrientation == 270) {
                displayOrientation = 180;
            }
            Log.d(LOG_TAG, "detectRotationView rotation 90, cameraOrientation " + cameraOrientation);

        }

        if (rotationView == Surface.ROTATION_270) {
            detectScreenOrientation270 = true;

            if (cameraOrientation == 90) {
                displayOrientation = 180;
            }
            if (cameraOrientation == 270) {
                displayOrientation = 0;
            }
            Log.d(LOG_TAG, "detectRotationView rotation 270, cameraOrientation " + cameraOrientation);
        }

        Log.d(LOG_TAG, "detectRotationView rotationPreview " + rotationView +
                " displayOrientation " + displayOrientation);

    }

    */

}
