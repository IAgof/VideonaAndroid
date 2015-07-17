/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;

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
     * @param cameraEffectFxList the list of the available songs
     */
    void showCameraEffectFxList(List<CameraEffectFxList> cameraEffectFxList);

    /**
     * Shows the list of the available songs.
     *
     * @param cameraEffectFxListList the list of the available songs
     */
    void reloadCameraEffectFxList(List<CameraEffectFxList> cameraEffectFxListList);

    /**
     * Checks if the list of the available songs is empty.
     */
    boolean isTheListEmpty();

    /**
     * Add new songs to the available songs.
     *
     * @param cameraEffectFxList the list of the available songs
     */
    void appendCameraEffectFxList(List<CameraEffectFxList> cameraEffectFxList);

}
