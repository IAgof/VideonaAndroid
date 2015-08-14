package com.videonasocialmedia.videona.record.domain;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.videonasocialmedia.videona.record.events.FrameAvailableEvent;
import com.videonasocialmedia.videona.record.events.PreviewSurfaceCreatedEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.greenrobot.event.EventBus;

/**
 * Created by jca on 13/8/15.
 */
public class CameraRenderer implements GLSurfaceView.Renderer {

    private SurfaceTexture surfaceTexture;
    private EventBus eventBus;

    public CameraRenderer() {
        eventBus = EventBus.getDefault();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        eventBus.post(new PreviewSurfaceCreatedEvent(surfaceTexture));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        eventBus.post(new FrameAvailableEvent(surfaceTexture));
    }
}
