package com.videonasocialmedia.videona.record.events;

import android.graphics.SurfaceTexture;

/**
 * Created by jca on 13/8/15.
 */
public class PreviewSurfaceCreatedEvent {
    public final SurfaceTexture surfaceTexture;

    public PreviewSurfaceCreatedEvent(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }
}
