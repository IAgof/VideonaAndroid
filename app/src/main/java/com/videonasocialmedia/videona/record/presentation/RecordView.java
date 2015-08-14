package com.videonasocialmedia.videona.record.presentation;

import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectColorList;
import com.videonasocialmedia.videona.presentation.views.adapter.CameraEffectFxList;

import java.util.List;

/**
 * Created by jca on 10/8/15.
 */
public interface RecordView {

    void showStopButton();

    void showRecButton();

    void startChronometer();

    void stopChronometer();

    void showRecordingIndicator();

    void hideRecordingIndicator();

    void showFxList(List<CameraEffectFxList> effects);

    void hideFxList();

    void showFiltersList(List<CameraEffectColorList> filters);

    void hideFiltersList();

    void navigateEditActivity(String durationVideoRecorded);

    void lockScreenRotation();

    void lockNavigator();

    void unLockNavigator();

    void showCameraSettingsMenu(boolean isFlashSupported,boolean isChangeCameraSupported);

    void hideCameraSettingsMenu();

    void showFlashActivated(boolean mode);

    void showError(String errorMesage);

    void showError(int errorMessageResourceId);
}
