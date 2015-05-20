/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

import java.util.List;

/**
 * This interface is used to show the music gallery.
 */
public interface MusicGalleryView {

    /**
     * Shows a loading message.
     */
    void showLoading();

    /**
     * Hides the loading message.
     */
    void hideLoading();

    /**
     * Shows the list of the available songs.
     *
     * @param musicList the list of the available songs
     */
    void showMusic(List<Music> musicList);

    /**
     * Checks if the list of the available songs is empty.
     */
    boolean isTheListEmpty();

    /**
     * Add new songs to the available songs.
     *
     * @param musicList the list of the available songs
     */
    void appendMusic(List<Music> musicList);

}
