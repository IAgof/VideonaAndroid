package com.videonasocialmedia.avrecorder.view.helper;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.videonasocialmedia.avrecorder.AVRecorder;

/**
 * Created by jca on 23/9/15.
 */
public class CameraOrientationHelper extends OrientationEventListener {

    AVRecorder recorder;
    int currenScreentOrientation = Surface.ROTATION_0;
    int currentCameraRotation = Surface.ROTATION_0;


    public CameraOrientationHelper(Context context, int rate, AVRecorder recorder) {
        super(context, rate);
        this.recorder = recorder;

    }

    public CameraOrientationHelper(Context context, AVRecorder recorder) {
        super(context);
        this.recorder = recorder;

    }


    @Override
    public void onOrientationChanged(int orientation) {
        updateCurrentScreenOrientation(orientation);
    }

    private int calculateScreenOrientation(int orientation) {
        return 0;
    }

    private void updateCurrentScreenOrientation(int orientation) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        int cameraIndex = recorder.getActiveCameraIndex();
        Camera.getCameraInfo(cameraIndex, info);
        int rotation = currentCameraRotation;
        int degrees = 90;
        switch (rotation) {
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //recorder.changeCameraOrientation(result);

        //cameraInfoOrientation = result;
        Log.d("OrientationHelper", "cameraInfoOrientation " + result + " cameraId " + cameraIndex);

    }
}
