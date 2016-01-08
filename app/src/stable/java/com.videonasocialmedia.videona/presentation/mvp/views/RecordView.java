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



import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;

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

    void showCameraEffectShader(List<Effect> effects);

    void showCameraEffectOverlay(List<Effect> effects);

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
}

