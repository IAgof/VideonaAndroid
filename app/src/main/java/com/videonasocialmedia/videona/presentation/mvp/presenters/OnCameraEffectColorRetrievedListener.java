/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColor;

import java.util.List;

/**
 * This interface is used for monitoring when the music items have been retrieved.
 */
public interface OnCameraEffectColorRetrievedListener {

    /**
     * This method is used when camera effects fx items have been retrieved.
     *
     * @param effectColorList the actual list of the available fx effects
     */
    void onCameraEffectColorRetrieved(List<CameraEffectColor> effectColorList);

    /**
     * This method is used when music items haven't been retrieved.
     */
    void onNoCameraEffectColorRetrieved();
}
