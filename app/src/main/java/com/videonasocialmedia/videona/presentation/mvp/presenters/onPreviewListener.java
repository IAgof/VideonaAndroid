/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.hardware.Camera;

import com.videonasocialmedia.videona.presentation.views.CameraPreview;

public interface onPreviewListener {

    public void onPreviewStarted(Camera camera, CameraPreview cameraPreview);
}
