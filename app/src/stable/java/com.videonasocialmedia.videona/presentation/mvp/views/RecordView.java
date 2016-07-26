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

import io.realm.RealmResults;

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

    void enableShareButton();

    void disableShareButton();

    void finishActivityForResult(String path);

    void showCameraEffectShader();

    void showCameraEffectOverlay();

    void updateShaderEffectList(RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> shaderEffects);

    void updateOverlayEffectList(RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> overlayEffects);

}

