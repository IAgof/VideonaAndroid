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
 * This interface is used for monitoring when the new items have been added to the actual track.
 *
 * @author vlf
 * @since 29/04/2015
 */
public interface OnAddAudioFinishedListener {
    /**
     * This method is used when fails to add a new item to the track.
     */
    public void onAddAudioItemToTrackError();

    /**
     * This method is used when new items have been added to the track.
     *
     * @param audioTrack the actual track with the new items added
     */
    public void onAddAudioItemToTrackSuccess(ArrayList<AudioTrack> audioTrack);
}
