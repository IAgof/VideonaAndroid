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


import com.videonasocialmedia.videona.presentation.views.adapter.Effect;

import java.util.List;

public interface RecordView {

    void showRecordButton();

    void showStopButton();

    void showMenuOptions();

    void hideMenuOptions();

    void showChronometer();

    void hideChronometer();

    void hideRecordedVideoThumb();

    void showRecordedVideoThumb(String path);

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void startChronometer();

    void stopChronometer();

    void showCameraEffectFx(List<Effect> effects);

    void showCameraEffectColor(List<Effect> effects);

    void lockScreenRotation();

    void unlockScreenRotation();

    void reStartScreenRotation();

    void lockNavigator(); //en VideonaView

    void unLockNavigator(); //en VideonaView

    void showFlashOn(boolean on);

    void showFlashSupported(boolean state);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void showError(String errorMessage); //videonaView

    void showError(int stringResourceId); //videonaView
}