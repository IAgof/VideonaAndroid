/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

public interface OnExportFinishedListener {

    void onExportError(String error);

    void onExportSuccess(Video video);

}
