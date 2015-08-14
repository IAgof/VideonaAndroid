package com.videonasocialmedia.videona.record.domain;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.videonasocialmedia.videona.avrecorder.MicrophoneEncoder;
import com.videonasocialmedia.videona.record.events.PreviewSurfaceCreatedEvent;

import java.io.IOException;

/**
 * Created by jca on 10/8/15.
 */
public class AVRecorder {
    protected VideoEncoder videoEncoder;
    protected MicrophoneEncoder mMicEncoder;
    //Maybe the camera could be placed in the activity.
    private Camera camera;
    private boolean isRecording;

    public void onEvent(PreviewSurfaceCreatedEvent event) {
        bindPreviewSurfaceToCamera(event.surfaceTexture);

    }

    private void bindPreviewSurfaceToCamera(SurfaceTexture surface) {
        try {
            camera.setPreviewTexture(surface);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void startRecord() {

    }

    public void stopRecord() {

    }

    private void startVideoRecord() {

    }

    private void stopVideoRecord() {

    }

    private void startAudioRecord() {

    }

    private void stopAudioRecord() {

    }
}
