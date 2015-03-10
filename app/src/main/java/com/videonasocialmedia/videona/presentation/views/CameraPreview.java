/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.videonasocialmedia.videona.utils.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.UserPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    private static final String TAG = "CameraPreview";

    public static List<String> colorEffects;


    private Context mContext;

    private SurfaceHolder mHolder;

    private Camera mCamera;

    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;


    private static UserPreferences appPrefs;

    public Paint paint;
    private boolean drawingViewSet = true;
    private CameraPreview camPreview;

    private Rect touchArea = null;

    public CameraPreview(Context context, Camera camera) {

        super(context);

        mCamera = camera;


        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        // http://stackoverflow.com/questions/19577299/android-camera-preview-stretched


        // supported preview sizes

        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        for (Camera.Size str : mSupportedPreviewSizes)

            Log.e(TAG, " cameraPreview " + str.width + "/" + str.height);


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

        mHolder = getHolder();

        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        // get available color effects

        colorEffects = mCamera.getParameters().getSupportedColorEffects();


        appPrefs = new UserPreferences(context);


    }


    public void surfaceCreated(SurfaceHolder holder) {

        // The Surface has been created, now tell the camera where to draw the preview.

        try {

            mCamera.setPreviewDisplay(holder);

            mCamera.startPreview();

        } catch (IOException e) {

            Log.d("DEBUG", "Error setting camera preview: " + e.getMessage());

        }


    }


    public void surfaceDestroyed(SurfaceHolder holder) {

        // empty. Take care of releasing the Camera preview in your activity.


    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.e(TAG, "surfaceChanged => width=" + w + ", height=" + h);

        // If your preview can change or rotate, take care of those events here.

        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {

            // preview surface does not exist

            return;

        }


        // stop preview before making changes

        try {

            mCamera.stopPreview();

        } catch (Exception e) {

            // ignore: tried to stop a non-existent preview

        }


        // set preview size and make any resize, rotate or reformatting changes here

        // start preview with new settings

        try {


            Camera.Parameters parameters = mCamera.getParameters();

            Camera.Size size = getBestPreviewSize(w, h);

            // parameters.setPreviewSize(size.width, size.height);

            parameters.setPreviewSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);

            Log.e(TAG, "surfaceChanged getBestPreviewSize => width=" + size.width + ", height=" + size.height);


            // Focus_continuous doesn't work well on OnePlus One
            // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            // AutoFocus onTouch ON
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);


            mCamera.setParameters(parameters);

            mCamera.startPreview();


        } catch (Exception e) {

            Log.d(TAG, "Error starting camera preview: " + e.getMessage());

        }

    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);


        if (mSupportedPreviewSizes != null) {

            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

        }


    }


    private Camera.Size getBestPreviewSize(int width, int height)

    {

        Camera.Size result = null;

        Camera.Parameters p = mCamera.getParameters();

        for (Camera.Size size : p.getSupportedPreviewSizes()) {


            if (size.height == ConfigUtils.VIDEO_SIZE_HEIGHT && size.width <= ConfigUtils.VIDEO_SIZE_WIDTH) {

                Log.d("CameraPreview", "getBestPreviewSize selection height " + size.height + " width " + size.width);

                result = size;

                return result;

            }

            if (size.width <= width && size.height <= height) {

                if (result == null) {

                    result = size;

                } else {

                    int resultArea = result.width * result.height;

                    int newArea = size.width * size.height;


                    if (newArea > resultArea) {

                        result = size;

                    }

                }

            }

        }

        return result;


    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;

        double targetRatio = (double) h / w;


        if (sizes == null) return null;


        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;


        int targetHeight = h;


        for (Camera.Size size : sizes) {

            double ratio = (double) size.width / size.height;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {

                optimalSize = size;

                minDiff = Math.abs(size.height - targetHeight);

            }

        }


        if (optimalSize == null) {

            minDiff = Double.MAX_VALUE;

            for (Camera.Size size : sizes) {

                if (Math.abs(size.height - targetHeight) < minDiff) {

                    optimalSize = size;

                    minDiff = Math.abs(size.height - targetHeight);

                }

            }

        }

        return optimalSize;

    }


    // Blog reference to Focus Area http://www.jayrambhia.com/blog/android-touchfocus/
    // https://github.com/jayrambhia/Touch2Focus/tree/master/src/com/fenchtose/touch2focus

    /**
     * Called from PreviewSurfaceView to set touch focus.
     *
     * @param - Rect - new area for auto focus
     */
    public void doTouchFocus(final Rect tfocusRect) {
        Log.i(TAG, "TouchFocus");
        try {
            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);
            Camera.Parameters para = mCamera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            mCamera.setParameters(para);
            mCamera.autoFocus(myAutoFocusCallback);

            touchArea = tfocusRect;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Unable to autofocus");
        }
    }

    /**
     * AutoFocus callback
     */
    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0) {
                mCamera.cancelAutoFocus();
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
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


        }
        return false;
    }

}
