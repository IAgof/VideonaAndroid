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
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.videonasocialmedia.videona.utils.ConfigUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    private static final String LOG_TAG = "CameraPreview";

    public static List<String> colorEffects;

    private SurfaceHolder mHolder;

    private Camera mCamera;

    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;

    public Paint paint;
    private boolean drawingViewSet = true;
    private CameraPreview camPreview;

    private Rect touchArea = null;

    private int rotationView;

    private boolean detectScreenOrientation90 = true;
    private boolean detectScreenOrientation270 = true;

    private boolean supportAutoFocus = true;

    private boolean hasZoom = false;
    private int zoomFactor = 0;
    private int maxZoomFactor = 0;
    private List<Integer> zoomRatios = null;

    private ScaleGestureDetector scaleGestureDetector;

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

            Log.e(LOG_TAG, " cameraPreview " + str.width + "/" + str.height);


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

        mHolder = getHolder();

        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        // get available color effects

        colorEffects = mCamera.getParameters().getSupportedColorEffects();

        checkCameraZoom(camera);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

    }

    private void checkCameraZoom(Camera camera) {

        Camera.Parameters parameters = camera.getParameters();

        hasZoom = parameters.isZoomSupported();

        Log.d(LOG_TAG, "checkCameraZoom hasZoom " + hasZoom );

        if(hasZoom){
            maxZoomFactor = parameters.getMaxZoom();

            try {
                zoomRatios = parameters.getZoomRatios();
            }
            catch(NumberFormatException e) {
                // crash java.lang.NumberFormatException: Invalid int: " 500" reported in v1.4 on device "es209ra", Android 4.1, 3 Jan 2014
                // this is from java.lang.Integer.invalidInt(Integer.java:138) - unclear if this is a bug in Open Camera, all we can do for now is catch it
                Log.e(LOG_TAG, "NumberFormatException in getZoomRatios()");
                e.printStackTrace();
                hasZoom = false;
                zoomRatios = null;
            }

        }
    }

    public void setCameraOrientation(int cameraOrientation){

        mCamera.setDisplayOrientation(cameraOrientation);

    }


    public void surfaceCreated(SurfaceHolder holder) {

        // The Surface has been created, now tell the camera where to draw the preview.

        try {

            // must be done after setting parameters, as this function may set parameters
            if( this.hasZoom && zoomFactor != 0 ) {
                int new_zoom_factor = zoomFactor;
                zoomFactor = 0; // force zoomTo to actually update the zoom!
                zoomTo(new_zoom_factor, true);
            }

            mCamera.setPreviewDisplay(holder);

            mCamera.startPreview();

        } catch (IOException e) {

            Log.d("DEBUG", "Error setting camera preview: " + e.getMessage());

        }


    }


    public void surfaceDestroyed(SurfaceHolder holder) {

        // empty. Take care of releasing the Camera preview in your activity.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.d(LOG_TAG, "surfaceChanged => width=" + w + ", height=" + h);

        // If your preview can change or rotate, take care of those events here.

        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {

            // preview surface does not exist

            Log.d("LOG_TAG", "la superficie no ha cambiado");
            return;

        }


        // stop preview before making changes
        // set preview size and make any resize, rotate or reformatting changes here
        // start preview with new settings
        try {

            mCamera.stopPreview();

            Camera.Parameters parameters = mCamera.getParameters();

            Camera.Size size = getBestPreviewSize(w, h);

            // parameters.setPreviewSize(size.width, size.height);

            ///TODO get VideoSize from model, Project Profile
            parameters.setPreviewSize(ConfigUtils.VIDEO_SIZE_WIDTH, ConfigUtils.VIDEO_SIZE_HEIGHT);
            //parameters.setPreviewSize(size.width, size.height);

            Log.d(LOG_TAG, "surfaceChanged getBestPreviewSize => width=" + size.width + ", height=" + size.height);


            // Focus_continuous doesn't work well on OnePlus One
            // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            // AutoFocus onTouch ON
            if(supportAutoFocus(mCamera)) {
               parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }


            mCamera.setParameters(parameters);

            mCamera.startPreview();

        } catch (Exception e) {

            // ignore: tried to stop a non-existent preview
            Log.e(LOG_TAG, "Error starting camera preview: ", e);

        }
    }

    private boolean supportAutoFocus(Camera mCamera) {

        Camera.Parameters parameters = mCamera.getParameters();

        for(String autoFocus: parameters.getSupportedFocusModes()){
            if(autoFocus.compareTo(Camera.Parameters.FOCUS_MODE_AUTO) == 0){
                Log.d(LOG_TAG, "Autofocus supported");
                supportAutoFocus = true;
                return true;
            }
        }

        supportAutoFocus = false;
        return false;

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
        Log.i(LOG_TAG, "TouchFocus");

        if(supportAutoFocus) {

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
                Log.i(LOG_TAG, "Unable to autofocus");
            }
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

        scaleGestureDetector.onTouchEvent(event);

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

        return true;

    }


    // Zoom camera, from OpenCamera app, Preview.java
    public void scaleZoom(float scaleFactor) {

        Log.d(LOG_TAG, "scaleZoom() " + scaleFactor);

        if( mCamera != null && hasZoom ) {
            float zoomRatio = zoomRatios.get(zoomFactor)/100.0f;
            zoomRatio *= scaleFactor;

            int newZoomFactor = zoomFactor;
            if( zoomRatio <= 1.0f ) {
                newZoomFactor = 0;
            }
            else if( zoomRatio >= zoomRatios.get(maxZoomFactor)/100.0f ) {
                newZoomFactor = maxZoomFactor;
            }
            else {
                // find the closest zoom level
                if( scaleFactor > 1.0f ) {
                    // zooming in
                    for(int i=zoomFactor;i<zoomRatios.size();i++) {
                        if( zoomRatios.get(i)/100.0f >= zoomRatio ) {
                            Log.d(LOG_TAG, "zoom int, found new zoom by comparing " + zoomRatios.get(i)/100.0f + " >= " + zoomRatio);
                            newZoomFactor = i;
                            break;
                        }
                    }
                }
                else {
                    // zooming out
                    for(int i=zoomFactor;i>=0;i--) {
                        if( zoomRatios.get(i)/100.0f <= zoomRatio ) {
                                Log.d(LOG_TAG, "zoom out, found new zoom by comparing " + zoomRatios.get(i)/100.0f + " <= " + zoomRatio);
                            newZoomFactor = i;
                            break;
                        }
                    }
                }
            }

                Log.d(LOG_TAG, "ScaleListener.onScale zoom_ratio is now " + zoomRatio);
                Log.d(LOG_TAG, "    old zoom_factor " + zoomFactor + " ratio " + zoomRatios.get(zoomFactor)/100.0f);
                Log.d(LOG_TAG, "    chosen new zoom_factor " + newZoomFactor + " ratio " + zoomRatios.get(newZoomFactor)/100.0f);

            zoomTo(newZoomFactor, true);
        }
    }

    public void zoomTo(int newZoomFactor, boolean update_seek_bar) {
        
        Log.d(LOG_TAG, "ZoomTo(): " + newZoomFactor);
        if( newZoomFactor < 0 )
            newZoomFactor = 0;
        if( newZoomFactor > maxZoomFactor )
            newZoomFactor = maxZoomFactor;
        // problem where we crashed due to calling this function with null camera should be fixed now, but check again just to be safe
        if(newZoomFactor != zoomFactor && mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if( parameters.isZoomSupported() ) {
                Log.d(LOG_TAG, "zoom was: " + parameters.getZoom());
                parameters.setZoom((int) newZoomFactor);
                try {
                    mCamera.setParameters(parameters);
                    zoomFactor = newZoomFactor;
                   
                }
                catch(RuntimeException e) {
                    // crash reported in v1.3 on device "PANTONE 5 SoftBank 107SH (SBM107SH)"
                     Log.e(LOG_TAG, "runtime exception in ZoomTo()");
                    e.printStackTrace();
                }
               // clearFocusAreas();
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(LOG_TAG, "ScaleListener, scaleZoom ");
            if( mCamera != null && hasZoom ) {
                Log.d(LOG_TAG, "ScaleListener, scaleZoom hasZoom ");
                scaleZoom(detector.getScaleFactor());
            }
            return true;
        }
    }

}
