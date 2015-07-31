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


import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColorList;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;

import java.util.List;

public interface RecordView extends MVPView {

    void showRecordStarted();

    void showRecordFinished();

    void startChronometer();

    void stopChronometer();

    void showCameraEffectFx(List<CameraEffectFxList> effects);

    void showCameraEffectFxSelected(String colorEffect);

    void showCameraEffectColor(List<CameraEffectColorList> effects);

    void showCameraEffectColorSelected(String colorEffect);

    void navigateEditActivity(String durationVideoRecorded);

    void lockScreenRotation();

    void lockNavigator();

    void unLockNavigator();

    void showSettingsCamera(boolean isFlashSupported,boolean isChangeCameraSupported);

    void showFlashModeTorch(boolean mode);

    void showCamera(int cameraMode);

    void showThumbVideoRecorded(String videoPath);

    void changeCamera();

    void showError();

    void reStartFragment();

}
