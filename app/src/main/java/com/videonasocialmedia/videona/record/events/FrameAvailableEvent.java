package com.videonasocialmedia.videona.record.events;

import android.graphics.SurfaceTexture;

/**
 * Created by jca on 13/8/15.
 */
public class FrameAvailableEvent {
   public final SurfaceTexture surfaceTexture;

    public FrameAvailableEvent(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }
}
