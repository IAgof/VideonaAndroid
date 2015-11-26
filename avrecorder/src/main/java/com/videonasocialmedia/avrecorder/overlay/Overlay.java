package com.videonasocialmedia.avrecorder.overlay;

import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.videonasocialmedia.avrecorder.FullFrameRect;
import com.videonasocialmedia.avrecorder.GlUtil;
import com.videonasocialmedia.avrecorder.Texture2dProgram;

/**
 * Created by jca on 25/11/15.
 */
public class Overlay {

    private float[] IDENTITY_MATRIX = new float[16];

    private FullFrameRect overlayLayer;

    private int height;
    private int width;
    private int positionX;
    private int positionY;

    private int textureId;


    public Overlay(Drawable overlayImage,int height, int width, int positionX, int positionY) {
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
        textureId = GlUtil.createTextureFromDrawable(overlayImage);
        this.height = height;
        this.width = width;
        this.positionX = positionX;
        this.positionY = positionY;
        final Texture2dProgram program =
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D);
        program.setTexSize(width, height);
        overlayLayer = new FullFrameRect(program);
    }

    public void setGlViewportSize(){
        GLES20.glViewport(positionX,positionY,width,height);
    }
    public void draw() {
        setGlViewportSize();
        overlayLayer.drawFrame(textureId, IDENTITY_MATRIX);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
