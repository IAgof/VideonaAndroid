/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.export;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by alvaro on 27/07/16.
 */
public interface OnExportEndedListener {
    void onExportError(String error);

    void onExportSuccess(Video video);
}
