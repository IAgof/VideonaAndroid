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

import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

/**
 * This interface is used for monitoring when the new items have been added to the actual track.
 */
public interface OnAddMediaFinishedListener {
    /**
     * This method is used when fails to add a new item to the track.
     */
    public void onAddMediaItemToTrackError();

    /**
     * This method is used when new items have been added to the track.
     *
     * @param mediaTrack the actual track with the new items added
     */
    public void onAddMediaItemToTrackSuccess(MediaTrack mediaTrack);
}
