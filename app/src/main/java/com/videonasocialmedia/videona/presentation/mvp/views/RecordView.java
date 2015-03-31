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


import android.hardware.Camera;

import com.videonasocialmedia.videona.presentation.views.CameraPreview;

public interface RecordView extends MVPView {

    void startPreview(Camera camera, CameraPreview cameraPreview);

    void startRecordVideo();

    void stopRecordVideo();

    void setChronometer();

    void startChronometer();

    void stopChronometer();

    void colorEffect(int position, Camera camera);

}
