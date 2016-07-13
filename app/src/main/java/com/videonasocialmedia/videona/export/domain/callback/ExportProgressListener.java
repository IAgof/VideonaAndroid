/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.domain.callback;


/**
 * Created by alvaro on 11/07/16.
 */
public interface ExportProgressListener {

    void onExportSuccessFinished(String mediaPath);

    void onExportError(String error);

    void onExportProgressUpdated(int progress);
}
