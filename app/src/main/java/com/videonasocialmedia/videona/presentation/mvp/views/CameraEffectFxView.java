/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFx;

import java.util.List;

/**
 * This interface is used to show the music gallery.
 */
public interface CameraEffectFxView {

    /**
     * Shows a loading message.
     */
    void showLoading();

    /**
     * Hides the loading message.
     */
    void hideLoading();

    /**
     * Shows the list of the available songs.
     *
     * @param cameraEffectFx the list of the available songs
     */
    void showCameraEffectFxList(List<CameraEffectFx> cameraEffectFx);

    /**
     * Shows the list of the available songs.
     *
     * @param cameraEffectFxList the list of the available songs
     */
    void reloadCameraEffectFxList(List<CameraEffectFx> cameraEffectFxList);

    /**
     * Checks if the list of the available songs is empty.
     */
    boolean isTheListEmpty();

    /**
     * Add new songs to the available songs.
     *
     * @param cameraEffectFx the list of the available songs
     */
    void appendCameraEffectFxList(List<CameraEffectFx> cameraEffectFx);

}
