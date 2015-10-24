package com.videonasocialmedia.avrecorder.view;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidbrodsky on 1/30/14.
 */
public class GLCameraView extends GLSurfaceView {
    private static final String TAG = "GLCameraView";

    protected ScaleGestureDetector mScaleGestureDetector;
    private Camera mCamera;
    private int mMaxZoom;
    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        int mZoomWhenScaleBegan = 0;
        int mCurrentZoom = 0;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mCamera != null) {
                Camera.Parameters params = mCamera.getParameters();
                mCurrentZoom = (int) (mZoomWhenScaleBegan + (mMaxZoom * (detector.getScaleFactor() - 1)));
                mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
                mCurrentZoom = Math.max(0, mCurrentZoom);
                params.setZoom(mCurrentZoom);
                mCamera.setParameters(params);
            }

            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mZoomWhenScaleBegan = mCamera.getParameters().getZoom();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    public GLCameraView(Context context) {
        super(context);
        init(context);
    }

    public GLCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mMaxZoom = 0;

    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        Camera.Parameters camParams = mCamera.getParameters();
        if (camParams.isZoomSupported()) {
            mMaxZoom = camParams.getMaxZoom();
            mScaleGestureDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        }
    }

    public void releaseCamera() {
        mCamera = null;
        mScaleGestureDetector = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mCamera != null && ev.getPointerCount() == 1 && (ev.getAction() == MotionEvent.ACTION_DOWN))
            Log.d("Focus", "onTouchEvent doTouchFocus");

        if (mScaleGestureDetector != null) {
            if (!mScaleGestureDetector.onTouchEvent(ev)) {
                // No scale gesture detected

            }
        }

        if (supportAutoFocus(mCamera)) {

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

            doTouchFocus(targetFocusRect);
            return true;
        }

        return false;
    }

    private void doTouchFocus(final Rect tfocusRect) {

        try {

            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);
            Camera.Parameters para = mCamera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            mCamera.setParameters(para);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean arg0, Camera arg1) {
                    if (arg0) {
                        mCamera.cancelAutoFocus();
                    }
                }
            });

            //touchArea = tfocusRect;

        } catch (Exception e) {
            e.printStackTrace();
            // Log.i(LOG_TAG, "Unable to autofocus");
        }
    }

    private boolean supportAutoFocus(Camera mCamera) {

        boolean result = false;

        if(mCamera != null){

            Camera.Parameters parameters = mCamera.getParameters();

            for (String autoFocus : parameters.getSupportedFocusModes()) {
                if (autoFocus.compareTo(Camera.Parameters.FOCUS_MODE_AUTO) == 0) {
                    //Log.d(LOG_TAG, "Autofocus supported");
                    result = true;
                }
            }
        }
        return result;
    }
}
