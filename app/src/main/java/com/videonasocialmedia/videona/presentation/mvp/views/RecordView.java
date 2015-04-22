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


public interface RecordView extends MVPView {

    void startRecordVideo();

    void stopRecordVideo();

    void startChronometer();

    void stopChronometer();

    void colorEffect(int position);

}
