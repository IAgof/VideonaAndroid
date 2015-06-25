/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.util.Log;

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

import java.io.IOException;
import java.util.ArrayList;

public class RecordPresenter implements OnCameraEffectListener, OnColorEffectListener,
        OnFlashModeListener, OnSettingsCameraListener, OnRemoveMediaFinishedListener, OnAddMediaFinishedListener  {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private final RecordView recordView;
    private final RecordUseCase recordUseCase;

    /**
     * Add Media to Project Use Case
     */
    AddVideoToProjectUseCase addVideoToProjectUseCase;

    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;

    private SessionConfig mConfig;

    protected CameraEncoder mCamEncoder;
    protected MicrophoneEncoder mMicEncoder;

    private boolean mIsRecording;

    /**
     * Boolean, control flash mode, pressed or not
     */
    private boolean isFlahModeON = false;

    public RecordPresenter(RecordView recordView, SessionConfig config) throws IOException {

        this.recordView = recordView;
        mConfig = config;
        recordUseCase = new RecordUseCase();

        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        removeMusicFromProjectUseCase= new RemoveMusicFromProjectUseCase();

        try {
            init(getSessionConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(SessionConfig config) throws IOException {
        mCamEncoder = new CameraEncoder(config);
        mMicEncoder = new MicrophoneEncoder(config);
        mConfig = config;
        mIsRecording = false;

    }

    public SessionConfig getSessionConfig() {
        return mConfig;
    }

    public void setPreviewDisplay(GLCameraView display){
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

    public void adjustVideoBitrate(int targetBitRate){
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

        recordView.showRecordStarted();
        recordView.lockScreenRotation();
        recordView.lockNavigator();
        recordView.startChronometer();

        Log.d(LOG_TAG, "onRecordStarted");
    }

    public boolean isRecording(){
        return mIsRecording;
    }

    public void stopRecording(){
        mIsRecording = false;
        mMicEncoder.stopRecording();
        mCamEncoder.stopRecording();

        recordView.showRecordFinished();
        recordView.stopChronometer();
        recordView.unLockNavigator();

        Log.d(LOG_TAG, "onRecordStopped");
        clearProject();
        addVideoToProjectUseCase.addVideoToTrack(mConfig.getOutputPath(), this);
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
    public void reset(SessionConfig config) throws IOException {
        mCamEncoder.reset(config);
        mMicEncoder.reset(config);
        mConfig = config;
        mIsRecording = false;
    }

    /**
     * Release resources. Must be called after {@link #stopRecording()} After this call
     * this instance may no longer be used.
     */
    public void release() {
        mCamEncoder.release();
        // MicrophoneEncoder releases all it's resources when stopRecording is called
        // because it doesn't have any meaningful state
        // between recordings. It might someday if we decide to present
        // persistent audio volume meters etc.
        // Until then, we don't need to write MicrophoneEncoder.release()
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
    public void settingsCameraListener(){

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
}
