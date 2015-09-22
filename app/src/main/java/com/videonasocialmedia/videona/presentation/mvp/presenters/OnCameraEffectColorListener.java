/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColor;

import java.util.List;

public interface OnCameraEffectColorListener {

    void onCameraEffectColorAdded(String cameraEffectColor, long time);

    void onCameraEffectColorRemoved(String cameraEffectColor, long time);

    void onCameraEffectColorListRetrieved(List<CameraEffectColor> effects);

}
