/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
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
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColorList;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Álvaro Martínez Marco
 */

public class RecordPresenter implements OnCameraEffectFxListener, OnCameraEffectColorListener,
        OnFlashModeListener, OnSettingsCameraListener, OnRemoveMediaFinishedListener,
        OnAddMediaFinishedListener, OnSessionConfigListener,
        AndroidEncoder.OnMuxerFinishedEventListener, OnRecordEventListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter"; //getClass().getSimpleName();

    /**
     * RecordView
     */
    private final RecordView recordView;
    /**
     * Record use case, domain
     */
    private final RecordUseCase recordUseCase;

    /**
     * SessionConfig, configure muxer and audio and video settings
     */
    private SessionConfig mConfig;

    /**
     * Add Media to Project Use Case
     */
    AddVideoToProjectUseCase addVideoToProjectUseCase;

    /**
     * Remove video to project use case
     */
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;

    /**
     * CameraEncoder, control camera and video recording
     */
    protected CameraEncoder mCamEncoder;

    /**
     * MicrophoneEncoder, control audio recording
     */
    protected MicrophoneEncoder mMicEncoder;

    /**
     * Boolean, is video recording
     */
    private boolean mIsRecording;

    /**
     * Boolean, is encoder video finished to record
     */
    private boolean isEncoderVideoFinished = false;
    /**
     * Boolean, is encoder audio finished to record
     */
    private boolean isEncoderAudioFinished = false;
    /**
     * Boolean, is encoder muxer finished to record
     */
    private boolean isMuxerReleaseFinished = false;

    /**
     * Boolean, control flash mode, pressed or not
     */
    private boolean isFlahModeON = false;

    // Message AVRecorderHandler
    /**
     * Message, notify video has been recorded properly
     */
    public static final int MSG_AVRECORDER_FINISH = 100;
    /**
     * Message, something bad ocurred, refresh fragment
     */
    public static final int MSG_AVRECORDER_REFRESH = 200;

    /**
     * AVRecorderHandler, handler to control Android Muxer is finished, record file created
     * correctly, audio and video
     */
    private AVRecorderHandler mHandler = new AVRecorderHandler(this);
    /**
     * Boolean, audio error while recording.
     */
    private boolean onMuxerAudioError;

    private Context context;


    /**
     * Video duration int, needed for tracking
     */
    private String durationVideoRecorded = "";
    
    public RecordPresenter(final RecordView recordView, Context context) throws IOException {
        this.recordView = recordView;
        this.context = context;
        recordUseCase = new RecordUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();
    }


    /**
     *
     * Init CamEncoder and MicEncoder with config settings
     *
     * @param config
     * @throws IOException
     */
    private void init(SessionConfig config) throws IOException {
        mCamEncoder = new CameraEncoder(config, this, context);
        mMicEncoder = new MicrophoneEncoder(config, this);
        mIsRecording = false;
        mConfig = config;


    }

    /**
     * Init session config, audio and video settings.
     * Set from domail, use case. Get values from project, profile
     */
    public void initSessionConfig() {
        recordUseCase.initSessionConfig(this);
    }

    /**
     *  Get SessionConfig
     */
    public SessionConfig getSessionConfig() {
        return mConfig;
    }

    /**
     * Set preview display
     * @param display
     */
    public void setPreviewDisplay(GLCameraView display) {
        mCamEncoder.setPreviewDisplay(display);
    }

    /**
     * Apply video filter to CamEncoder
     * @param filter
     */
    public void setCameraEffectFx(int filter){
        mCamEncoder.applyFilter(filter);
    }

    /**
     * Request other camera, change front/back camera
     */
    public void requestOtherCamera(int rotation){
        mCamEncoder.requestOtherCamera(rotation);

    }

    /**
     * Request a specific camera, front or back
     * @param cameraId
     */
    public void requestCamera(int cameraId){
        mCamEncoder.requestCamera(cameraId);
    }

    /**
     * Toggle flas mode torch
     */
    public void toggleFlash(){
        mCamEncoder.toggleFlashMode();
    }

    /**
     * Adjust video bit rate
     * @param targetBitRate
     */
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

    /**
     * Start recording
     */
    public void startRecording(){

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File outputLocation = new File(Constants.PATH_APP_MASTERS, fileName);
        mConfig.setOutputDirectory(outputLocation);

        mIsRecording = true;
        mMicEncoder.startRecording();
        mCamEncoder.startRecording();

        recordUseCase.startRecording(this);

    }

    /**
     * Boolean isRecording
     * @return
     */
    public boolean isRecording(){
        return mIsRecording;
    }

    /**
     * Stop recording
     * //TODO Dummy recordUseCase
     */
    public void stopRecording(){
        mIsRecording = false;
        mMicEncoder.stopRecording();
        mCamEncoder.stopRecording();

        recordUseCase.stopRecording(this);

    }


    /**
     * PauseRecording.
     * // TODO study recording pause functionality
     * If while recording go to onPause, save video recording.
     */
    public void pauseRecording(){

        mIsRecording = false;

        mMicEncoder.stopRecording();

        mCamEncoder.stopRecording();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File fRecord = new File(Constants.PATH_APP_MASTERS, fileName);

        File fTempRecord = new File((recordUseCase.getOutputVideoPath()));

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Log.d(LOG_TAG, "pauseRecording renameFile" );
            retriever.setDataSource(fTempRecord.getAbsolutePath());

            fTempRecord.renameTo(fRecord);

        } catch (Exception e) {

        }

        recordView.reStartFragment();

    }

    /**
     * Clear project
     */
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
        mConfig = config;

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

    /**
     * onHostActivityPaused
     * RecordPresenter onPause, release camera and microphone
     */
    public void onHostActivityPaused(){
        mCamEncoder.onHostActivityPaused();
        if(mMicEncoder != null) {
            mMicEncoder.onHostActivityPaused();
            mMicEncoder = null;
        }
    }

    /**
     * onHostActivityResume
     * RecordPresenter onResume
     * Set camera and microphone available.
     */
    public void onHostActivityResumed(){

        mCamEncoder.onHostActivityResumed();

        if(mMicEncoder == null) {
            try {
                mMicEncoder = new MicrophoneEncoder(mConfig, this);
                Log.d(LOG_TAG, " create new mMicEncoder");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void reStartFragment() {

        Log.d(LOG_TAG, "reStartFragment MSG_AVRECORDER_REFRESH");

        recordView.reStartFragment();
    }


    /**
     * Settings camera button listener
     */
    public void settingsCameraListener() {

        recordUseCase.getSettingsCamera(this, mCamEncoder.getCamera());

    }

    /**
     * Effect, camera effects fx button pressed
     */
    public void cameraEffectFxClickListener() {

            recordUseCase.getAvailableCameraEffectFx(this);

    }

    /**
     * Effect, camera effects color button pressed
     */
    public void cameraEffectColorClickListener() {

            recordUseCase.getAvailableCameraEffectColor(this, mCamEncoder.getCamera());
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
     * Camera Effect color selected
     * @param position
     */
    //TODO Add effect use case
    public void setCameraEffectColor(int position) {
        recordUseCase.addAndroidCameraEffect(position, mCamEncoder.getCamera());
    }

    @Override
    public void onCameraEffectFxAdded(String cameraEffect, long time) {
        recordView.showCameraEffectFxSelected(cameraEffect);
      //  mCamEncoder.setCameraEffectFx(filter);

    }

    @Override
    public void onCameraEffectFxRemoved(String cameraEffect, long time) {
        //recordView.showCameraEffectFxSelected(cameraEffect);

    }

    @Override
    public void onCameraEffectFxListRetrieved(List<CameraEffectFxList> effects) {

        recordView.showCameraEffectFx(effects);
    }

    @Override
    public void onCameraEffectColorAdded(String cameraEffectColor, long time) {
        recordView.showCameraEffectColorSelected(cameraEffectColor);
    }

    @Override
    public void onCameraEffectColorRemoved(String cameraEffectColor, long time) {

    }

    @Override
    public void onCameraEffectColorListRetrieved(List<CameraEffectColorList> effects) {
        Log.d(LOG_TAG, "onCameraEffectColorListRetrieved()");

        recordView.showCameraEffectColor(effects);
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

        recordView.navigateEditActivity(durationVideoRecorded);

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
        Log.d(LOG_TAG, "onRecordStarted");

        recordView.showRecordStarted();
        recordView.lockScreenRotation();
        recordView.lockNavigator();
        recordView.startChronometer();

    }

    @Override
    public void onRecordStopped() {

        Log.d(LOG_TAG, "onRecordStopped");

        release();

        recordView.showRecordFinished();
        recordView.stopChronometer();
        recordView.unLockNavigator();

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
    public void onMuxerAudioError(String message){

        Log.d(LOG_TAG, "onMuxerAudioError " + message);

        if(mIsRecording){
            mIsRecording= false;
            mMicEncoder.stopRecording();
            mCamEncoder.stopRecording();
            mCamEncoder.release();
        }

        onMuxerAudioError = true;

        mHandler.sendMessage(mHandler.obtainMessage(MSG_AVRECORDER_REFRESH));


    }


    @Override
    public void onMuxerVideoError(String message){

        Log.d(LOG_TAG, "onMuxerVideoError " + message);

        mIsRecording=false;

        mHandler.sendMessage(mHandler.obtainMessage(MSG_AVRECORDER_REFRESH));

    }

    @Override
    public void onMuxerReleased(){

        Log.d(LOG_TAG, "onMuxerReleased");


        isMuxerReleaseFinished = true;

        if(isEncoderVideoFinished && isEncoderAudioFinished) {
            checkFinishEncoder();
        } else {
           // Do something
        }

    }

    private boolean checkFinishEncoder(){

        if(isEncoderVideoFinished && isEncoderAudioFinished && isMuxerReleaseFinished){

            if(onMuxerAudioError){
                mHandler.sendMessage(mHandler.obtainMessage(MSG_AVRECORDER_REFRESH));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_AVRECORDER_FINISH));
                return true;
            }
        }
        return false;
    }

    /**
     * setMsgAvrecorderFinish
     * Video finished. Video generated properly. Rename file and add video to project.
     * //TODO Set duration to tracker
     */
    public void setMsgAvrecorderFinish() {

        Log.d(LOG_TAG, "MSG_AVRECORDER_FINISH, setMsgAvrecorderFinish");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        File fRecord = new File(Constants.PATH_APP_MASTERS, fileName);
        File fTempRecord = new File((recordUseCase.getOutputVideoPath()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        
            try {
                Log.d("SHOW TIME TAG", "" );
                retriever.setDataSource(fTempRecord.getAbsolutePath());
                String duration = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION);
                //int durationInt = Integer.parseInt(duration);
                Log.d(LOG_TAG, "duration video recorded " + duration);
                durationVideoRecorded = duration;
                fTempRecord.renameTo(fRecord);
                addVideoToProjectUseCase.addVideoToTrack(fRecord.getAbsolutePath(), this);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Video has not been recorded properly");
            }

    }


    /**
     * AVRecorderHandler
     * Handler class to manage event from AndroidMuxer. If video, audio and muxer has been released,
     * setMsgAvrecorderFinish. If some error happen reStartFragment.
     */
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
                    Log.d(LOG_TAG, "MSG_AVRECORDER_FINISH");
                    break;
                case MSG_AVRECORDER_REFRESH:
                    recordPresenter.reStartFragment();
                    Log.d(LOG_TAG, "MSG_AVRECORDER_REFRESH");
                    break;
                default:
                    //recordPresenter.reStartFragment();
                    throw new RuntimeException("Unexpected msg what=" + what);

            }
        }
    }


}
