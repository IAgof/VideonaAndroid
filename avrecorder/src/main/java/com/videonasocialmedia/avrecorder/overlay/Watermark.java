package com.videonasocialmedia.avrecorder.overlay;

import android.graphics.drawable.Drawable;
import android.opengl.GLES20;

/**
 * Created by jca on 1/12/15.
 */
public class Watermark extends Overlay{

    public Watermark(Drawable overlayImage, int height, int width, int positionX, int positionY) {
        super(overlayImage, height, width, positionX, positionY);
    }

    @Override
    protected void setBlendMode() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }
}
