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

public interface RecordView {

    void showRecordButton();

    void showStopButton();

    void showSettings();

    void hideSettings();

    void showChronometer();

    void hideChronometer();

    void showRecordedVideoThumb(String path);

    void hideRecordedVideoThumb();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void startChronometer();

    void stopChronometer();

    void showCameraEffectFx(List<CameraEffectFx> effects);

    void showCameraEffectColor(List<CameraEffectColor> effects);

    void lockScreenRotation();

    void unlockScreenRotation();

    void reStartScreenRotation();

    void showFlashOn(boolean on);

    void showFlashSupported(boolean state);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void showError(String errorMessage); //videonaView

    void showError(int stringResourceId); //videonaView

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void showMessage(int stringToast);

}
