package com.videonasocialmedia.avrecorder.overlay;

import android.graphics.drawable.Drawable;
import android.opengl.GLES20;

/**
 * Created by alvaro on 8/06/16.
 */
public class BollywoodFilter extends Filter{

    public BollywoodFilter(Drawable filterImage, int height, int width) {
        super(filterImage, height, width);
    }

    @Override
    protected void setBlendMode() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }
}