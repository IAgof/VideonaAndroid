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


import com.videonasocialmedia.videona.presentation.views.CameraPreview;

import java.util.ArrayList;

public interface RecordView extends MVPView {

    void startPreview(CameraPreview cameraPreview);

    void stopPreview(CameraPreview cameraPreview);

    void startRecordVideo();

    void stopRecordVideo();

    void startChronometer();

    void stopChronometer();

    void showEffects(ArrayList<String> effects);

    void showEffectSelected(String colorEffect);

    void navigateEditActivity(String videoRecordName);

}
