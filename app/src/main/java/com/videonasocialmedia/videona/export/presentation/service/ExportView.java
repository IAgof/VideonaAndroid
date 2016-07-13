/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.presentation.service;

/**
 * Created by alvaro on 11/07/16.
 */
public interface ExportView {

    void showNotification(boolean foreground);

    void setNotificationProgress(int progress);

    void hideNotification();

    void showMessage(String message);

    void onSuccessVideoExported(String mediaPath);
}
