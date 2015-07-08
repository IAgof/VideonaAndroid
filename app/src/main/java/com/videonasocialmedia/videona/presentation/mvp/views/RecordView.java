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


import java.util.ArrayList;

public interface RecordView extends MVPView {

    void showRecordStarted();

    void showRecordFinished();

    void startChronometer();

    void stopChronometer();

    void showEffects(ArrayList<String> effects);

    void showEffectSelected(String colorEffect);

    void showCameraEffects(ArrayList<String> effects);

    void showCameraEffectSelected(String colorEffect);

    void navigateEditActivity();

    void lockScreenRotation();

    void lockNavigator();

    void unLockNavigator();

    void showSettingsCamera(boolean isFlashSupported,boolean isChangeCameraSupported);

    void showFlashModeTorch(boolean mode);

    void showCamera(int cameraMode);

    void showError();

    void reStartFragment();

}
