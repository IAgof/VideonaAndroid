/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;

import java.util.List;

public interface OnCameraEffectFxListener {

    void onCameraEffectFxAdded(String cameraEffect, long time);

    void onCameraEffectFxRemoved(String cameraEffect, long time);

    void onCameraEffectFxListRetrieved(List<CameraEffectFxList> effects);

}
