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


import com.videonasocialmedia.videona.effects.domain.model.Effect;

import java.util.List;

import io.realm.RealmResults;

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

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void showMessage(int stringToast);

    void enableShareButton();

    void disableShareButton();

    void finishActivityForResult(String path);

    void showCameraEffectShader();

    void showCameraEffectOverlay();

    void updateShaderEffectList(List<Effect> shaderEffects);

    void updateOverlayEffectList(List<Effect> overlayEffects);
}