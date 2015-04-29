/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;

import java.util.ArrayList;

/**
 * This interface is used for monitoring when the new items have been removed from the actual track.
 *
 * @author vlf
 * @since 29/04/2015
 */
public interface OnRemoveAudioFinishedListener {
    /**
     * This method is used when fails to remove an item from the track.
     */
    public void onRemoveAudioItemFromTrackError();

    /**
     * This method is used when the item has been removed from the track.
     *
     * @param audioTrack the actual track with the new items added
     */
    public void onRemoveAudioItemFromTrackSuccess(ArrayList<AudioTrack> audioTrack);
}
