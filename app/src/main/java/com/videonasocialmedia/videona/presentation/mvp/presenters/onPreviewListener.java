/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.presentation.views.CameraPreview;

public interface onPreviewListener {

    public void onPreviewStarted(CameraPreview cameraPreview);

    public void onPreviewReStarted(CameraPreview cameraPreview);
}
