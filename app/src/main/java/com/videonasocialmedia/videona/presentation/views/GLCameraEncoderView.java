/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.videona.avrecorder.CameraEncoder;


/**
 * Special GLSurfaceView for use with CameraEncoder
 * The tight coupling here allows richer touch interaction
 */
public class GLCameraEncoderView extends GLCameraView {
    private static final String TAG = "GLCameraEncoderView";

    protected CameraEncoder mCameraEncoder;

    //AutoFocus
    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    public static boolean showDraw = false;

    float x = 0;
    float y = 0;

    public GLCameraEncoderView(Context context) {
        super(context);

    }

    public GLCameraEncoderView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setCameraEncoder(CameraEncoder encoder){
        mCameraEncoder = encoder;
        setCamera(mCameraEncoder.getCamera());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       // Log.d("Focus", "onTouchEvent");

        if(mScaleGestureDetector != null){
            mScaleGestureDetector.onTouchEvent(ev);

        }
        if(mCameraEncoder != null && ev.getPointerCount() == 1 && (ev.getAction() == MotionEvent.ACTION_MOVE)){
            mCameraEncoder.handleCameraPreviewTouchEvent(ev);
        }else if(mCameraEncoder != null && ev.getPointerCount() == 1 && (ev.getAction() == MotionEvent.ACTION_DOWN)){
            mCameraEncoder.handleCameraPreviewTouchEvent(ev);

                Log.d("Focus", "onTouchEvent doTouchFocus");

                float x = ev.getX();
                float y = ev.getY();
                Rect touchRect = new Rect(
                        (int) (x - 100),
                        (int) (y - 100),
                        (int) (x + 100),
                        (int) (y + 100));
                final Rect targetFocusRect = new Rect(
                        touchRect.left * 2000 / this.getWidth() - 1000,
                        touchRect.top * 2000 / this.getHeight() - 1000,
                        touchRect.right * 2000 / this.getWidth() - 1000,
                        touchRect.bottom * 2000 / this.getHeight() - 1000);

            mCameraEncoder.doTouchFocus(targetFocusRect);

        }
        return true;
      //  return false;
    }



}
