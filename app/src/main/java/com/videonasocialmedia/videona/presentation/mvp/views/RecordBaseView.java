package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.presentation.views.adapter.Effect;

import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 17/12/2015.
 */
public interface RecordBaseView {
    void showRecordButton();

    void showStopButton();

    void showChronometer();

    void hideChronometer();

    void showRecordedVideoThumb(String path);

    void hideRecordedVideoThumb();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void startChronometer();

    void stopChronometer();

    void showCameraEffectFx(List<Effect> effects);

    void showCameraEffectColor(List<Effect> effects);

    void lockScreenRotation();

    void unlockScreenRotation();

    void reStartScreenRotation();

    void showFlashOn(boolean on);

    void showFlashSupported(boolean state);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void showError(String errorMessage);

    void showError(int stringResourceId);

}
