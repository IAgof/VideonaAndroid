/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.CustomManualFocusView;

public interface OnChangeCameraListener {

    public void onChangeCameraSuccess(int cameraMode);

    public void onReleaseChangeCameraPreview(CameraPreview cameraPreview,
                                CustomManualFocusView customManualFocusView, boolean autoFocus);

    public void onChangeCameraError();
}
