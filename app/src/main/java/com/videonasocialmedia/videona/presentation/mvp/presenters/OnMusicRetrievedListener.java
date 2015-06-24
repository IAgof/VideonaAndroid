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

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

import java.util.List;

/**
 * This interface is used for monitoring when the music items have been retrieved.
 */
public interface OnMusicRetrievedListener {

    /**
     * This method is used when music items have been retrieved.
     *
     * @param musicList the actual list of the available songs
     */
    void onMusicRetrieved(List<Music> musicList);

    /**
     * This method is used when music items haven't been retrieved.
     */
    void onNoMusicRetrieved();
}
