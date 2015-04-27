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

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;

public interface RecordView extends MVPView {

    void startPreview(Camera camera, CameraPreview cameraPreview);

    void startRecordVideo();

    void stopRecordVideo();

    void setChronometer();

    void startChronometer();

    void stopChronometer();

    void showEffects(ColorEffectAdapter adapter);

    void showEffectSelected(Effect effect);

}
