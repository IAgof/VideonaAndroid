package com.videonasocialmedia.videona.record.domain;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMuxer;

import com.videonasocialmedia.videona.avrecorder.MicrophoneEncoder;
import com.videonasocialmedia.videona.record.events.PreviewSurfaceCreatedEvent;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by jca on 10/8/15.
 */
public class AVRecorder {

    private EncodingConfig config;
    private VideoEncoder videoEncoder;
    private MicrophoneEncoder mMicEncoder;
    private MediaMuxer muxer;

    //Maybe the camera could be placed in the activity.
    private Camera camera;
    private boolean isRecording;

    public void onEvent(PreviewSurfaceCreatedEvent event) {
        bindPreviewSurfaceToCamera(event.surfaceTexture);
    }


    public void initPreview(){
        EventBus.getDefault().register(this);
    }

    public void release(){
        EventBus.getDefault().unregister(this);
    }

    private void bindPreviewSurfaceToCamera(SurfaceTexture surface) {
        try {
            camera.setPreviewTexture(surface);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void startRecord(String outputPath) {

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
