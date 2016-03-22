package com.videonasocialmedia.avrecorder.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.videonasocialmedia.avrecorder.CameraEncoder;
import com.videonasocialmedia.avrecorder.CameraSurfaceRenderer;


/**
 * Special GLSurfaceView for use with CameraEncoder
 * The tight coupling here allows richer touch interaction
 */
public class GLCameraEncoderView extends GLCameraView {
    private static final String TAG = "GLCameraEncoderView";

    private final CameraSurfaceRenderer cameraSurfaceRenderer;
    protected CameraEncoder mCameraEncoder;

    public GLCameraEncoderView(Context context) {
        super(context);
        cameraSurfaceRenderer = new CameraSurfaceRenderer();
        initRenderer();
    }

    private void initRenderer() {
        setEGLContextClientVersion(2);
        setRenderer(cameraSurfaceRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setPreserveEGLContextOnPause(true);
    }

    public GLCameraEncoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cameraSurfaceRenderer = new CameraSurfaceRenderer();
        initRenderer();
    }

    public void setCameraEncoder(CameraEncoder encoder){
        mCameraEncoder = encoder;
//        cameraSurfaceRenderer.setEncoder(encoder);
        setCamera(mCameraEncoder.getCamera());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean supportFocus = super.onTouchEvent(ev);
        if(supportFocus) {
            if (mScaleGestureDetector != null) {
                mScaleGestureDetector.onTouchEvent(ev);
            }
        }
        if(mCameraEncoder != null && ev.getPointerCount() == 1 && (ev.getAction() == MotionEvent.ACTION_MOVE)){
           // mCameraEncoder.handleCameraPreviewTouchEvent(ev);
        }else if(mCameraEncoder != null && ev.getPointerCount() == 1 && (ev.getAction() == MotionEvent.ACTION_DOWN)){
           // mCameraEncoder.handleCameraPreviewTouchEvent(ev);
        }
        return true;
    }

    public CameraSurfaceRenderer getRenderer() {
        return cameraSurfaceRenderer;
    }


}
