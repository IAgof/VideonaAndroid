package com.videonasocialmedia.avrecorder.event;

import android.hardware.Camera.Parameters;

/**
 * Used to pass the parameters of the opened camera to subscribers.
 */
public class CameraOpenedEvent {

    public Parameters params;
    public int cameraInfoOrientation;

    public CameraOpenedEvent(Parameters params, int cameraInfoOrientation) {
        this.params = params;
        this.cameraInfoOrientation = cameraInfoOrientation;
    }

}
