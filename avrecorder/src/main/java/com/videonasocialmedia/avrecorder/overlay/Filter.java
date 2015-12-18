package com.videonasocialmedia.avrecorder.overlay;

import android.graphics.drawable.Drawable;
import android.opengl.GLES20;

/**
 * Created by jca on 1/12/15.
 */
public class Filter extends Overlay{

    public Filter(Drawable filterImage, int height, int width) {
        super(filterImage, height, width, 0, 0);
    }

    @Override
    protected void setBlendMode() {
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_COLOR);
    }

    @Override
    protected void setGlViewportSize() {
        //TODO set the viewport to full screen
    }

}
