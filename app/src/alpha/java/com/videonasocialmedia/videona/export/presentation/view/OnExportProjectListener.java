/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.presentation.view;

/**
 * Created by alvaro on 12/07/16.
 */
public interface OnExportProjectListener {

    void messageService(String message);

    void onSuccessVideoExported(String mediaPath);

    void startService();
}
