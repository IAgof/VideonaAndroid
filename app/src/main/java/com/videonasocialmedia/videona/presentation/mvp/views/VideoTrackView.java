/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

/**
 * Created by vlf on 27/04/2015.
 */
public interface VideoTrackView {
    public void showError(int errorMessageResource);

    public void drawMediaList(MediaTrack mediaTrack);
}
