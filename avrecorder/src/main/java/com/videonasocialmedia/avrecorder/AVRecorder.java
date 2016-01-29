package com.videonasocialmedia.avrecorder;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.avrecorder.view.GLCameraView;

import java.io.IOException;


/**
 * Records an Audio / Video stream to disk.
 * <p/>
 * Example usage:
 * <ul>
 * <li>AVRecorder recorder = new AVRecorder(mSessionConfig);</li>
 * <li>recorder.setPreviewDisplay(mPreviewDisplay);</li>
 * <li>recorder.startRecording();</li>
 * <li>recorder.stopRecording();</li>
 * <li>(Optional) recorder.reset(mNewSessionConfig);</li>
 * <li>(Optional) recorder.startRecording();</li>
 * <li>(Optional) recorder.stopRecording();</li>
 * <li>recorder.release();</li>
 * </ul>
 *
 * @hide
 */
public class AVRecorder {

    protected CameraEncoder mCamEncoder;
    protected MicrophoneEncoder mMicEncoder;
    private SessionConfig mConfig;
    private boolean mIsRecording;
    private boolean released;

    // To flash support
    private boolean cameraChanged;

    public AVRecorder(SessionConfig config, Drawable watermark) throws IOException {
        init(config);
        setWatermark(watermark);
    }

    public AVRecorder (SessionConfig config) throws IOException{
        init(config);
    }

    private void init(SessionConfig config) throws IOException {
        mCamEncoder = new CameraEncoder(config);
        mMicEncoder = new MicrophoneEncoder(config);
        mConfig = config;
        mIsRecording = false;
        released = false;
        cameraChanged = false;
    }

    public void setPreviewDisplay(GLCameraView display) {
        mCamEncoder.setPreviewDisplay(display);
        //TODO remove from here. this is just a test
    }

    /**
     * Apply a filter from {@link Filters} class
     *
     * @param filter
     */
    public void applyFilter(int filter) {
        mCamEncoder.applyFilter(filter);
    }

    /**
     * Add a layer to a stack to be draw over the previews layers
     *
     * @param image the image shown on the layer
     */
    public void addOverlayFilter(Drawable image) {
        mCamEncoder.addOverlayFilter(image, mConfig.getVideoWidth(), mConfig.getVideoHeight());
    }

    public void setWatermark(Drawable watermarkImage, boolean showInPreview) {
        mCamEncoder.addWatermark(watermarkImage, showInPreview);
    }

    /**
     * Set a watermark hide in the preview
     *
     * @param watermarkImage
     */
    public void setWatermark(Drawable watermarkImage) {
        mCamEncoder.addWatermark(watermarkImage, false);
    }

    //TODO define how to identify a single overlay;
    public void removeOverlay() {
        mCamEncoder.removeOverlayFilter(null);
    }

    /**
     * @return Return the camera code (i.e. 0 for back camera and 1 for front camera)
     */
    public int requestOtherCamera() {

        return mCamEncoder.requestOtherCamera();
    }

    public void requestCamera(int camera) {
        mCamEncoder.requestCamera(camera);
    }

    /**
     * @return whether the flash is on (true) or off (false)
     */
    public boolean toggleFlash() {
        return mCamEncoder.toggleFlashMode();
    }

    /**
     * @return whether the flash is on (true) or off (false)
     */
    public boolean setFlashOff() {
        return mCamEncoder.setFlashOff();
    }

    public int checkSupportFlash() {

        return mCamEncoder.checkSupportFlash();
    }

    public void adjustVideoBitrate(int targetBitRate) {
        mCamEncoder.adjustBitrate(targetBitRate);
    }

    /**
     * Signal that the recorder should treat
     * incoming video frames as Vertical Video, rotating
     * and cropping them for proper display.
     */
    public void signalVerticalVideo(FullFrameRect.SCREEN_ROTATION orientation) {
        mCamEncoder.signalVerticalVideo(orientation);
    }

    public void startRecording() {
        mIsRecording = true;
        mMicEncoder.startRecording();
        mCamEncoder.startRecording();
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void stopRecording() {
        Log.d("AVRecoder", "Stop: starting stop process");
        mMicEncoder.stopRecording();
        mCamEncoder.stopRecording();
        mIsRecording = false;
    }

    /**
     * Prepare for a subsequent recording. Must be called after {@link #stopRecording()}
     * and before {@link #release()}
     *
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
        mMicEncoder.release();
        mCamEncoder.release();
        released = true;

        // MicrophoneEncoder releases all it's resources when stopRecording is called
        // because it doesn't have any meaningful state
        // between recordings. It might someday if we decide to present
        // persistent audio volume meters etc.
        // Until then, we don't need to write MicrophoneEncoder.release()
    }

    public void onHostActivityPaused() {
        mCamEncoder.onHostActivityPaused();
        //mMicEncoder.onHostActivityPaused();
    }

    public void onHostActivityResumed() {
        mCamEncoder.onHostActivityResumed();
        //mMicEncoder.onHostActivityResumed();
    }

    public int getActiveCameraIndex() {
        return mCamEncoder.getCurrentCamera();
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isCameraChanged() {
        return cameraChanged;
    }

    public void rotateCamera(int rotation) {
        mCamEncoder.updateRotationDisplay(rotation);
    }
}
