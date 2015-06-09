/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;

/**
 * This interface is used for monitoring when the new items have been added to the actual track.
 */
public interface OnAddMediaFinishedListener {
    /**
     * This method is used when fails to add a new item to the track.
     */
    void onAddMediaItemToTrackError();

    /**
     * This method is used when new items have been added to the track.
     */
    void onAddMediaItemToTrackSuccess(Media media);
}
