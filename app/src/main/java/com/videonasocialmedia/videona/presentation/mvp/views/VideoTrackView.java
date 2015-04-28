/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

/**
 * This interface is used to update the track view in the editor activity.
 *
 * @author vlf
 * @since 27/04/2015
 */
public interface VideoTrackView {
    /**
     * Shows the error if the new item hasn't been added to the track.
     *
     * @param errorMessageResource
     */
    public void showError(int errorMessageResource);

    /**
     * Draws the media track with the new items.
     *
     * @param mediaTrack the media track with the new items added
     */
    public void drawMediaList(MediaTrack mediaTrack);
}
