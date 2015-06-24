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

/**
 * This interface is used for monitoring when the items have been deleted from the actual track.
 */
public interface OnRemoveMediaFinishedListener {
    /**
     * This method is used when fails to deleted items from the track.
     */
    void onRemoveMediaItemFromTrackError();

    /**
     * This method is used when items have been deleted from the track.
     */
    void onRemoveMediaItemFromTrackSuccess();
}

