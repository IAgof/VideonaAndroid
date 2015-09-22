/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.presentation.mvp.views;


import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColor;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFx;

import java.util.List;

public interface RecordView extends MVPView {

    void showRecordButton();

    void showStopButton();

    void startChronometer();

    void stopChronometer();

    void showCameraEffectFx(List<CameraEffectFx> effects);

    void showCameraEffectFxSelected(String colorEffect); //candidato a salir

    void showCameraEffectColor(List<CameraEffectColor> effects);

    void showCameraEffectColorSelected(String colorEffect); //candidato a salir

    void navigateEditActivity(String durationVideoRecorded);

    void lockScreenRotation();

    void lockNavigator(); //en VideonaView

    void unLockNavigator(); //en VideonaView

    /**
     *
     * @param on true=on, false=off
     */
    void showFlashOn(boolean on);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void changeCamera(); // candidato a salir

    void showError(String errorMessage); //videonaView

    void showError(int stringResourceId); //videonaView

    //Qué cojones tiene que ver esto con una recordView.
    // Las interfaces son ABSTRACCIONES deben ser generales e independientes de fragments y demás
    void reStartFragment(); //Este se va seguro!

    void showRecordedVideoThumb(String path);

    void showVideosRecordedNumber(int numberOfVideos);

}
