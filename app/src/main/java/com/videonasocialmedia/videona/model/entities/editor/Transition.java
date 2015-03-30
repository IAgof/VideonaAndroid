/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;

public class Transition {

    /**
     * The media after the transition
     */
    Media afterMediaItem;
    /**
     * The media before the transition
     */
    Media beforeMediaItem;

    /**
     * Length of the transition
     */
    long duration;


    public Transition(Media afterMediaItem, Media beforeMediaItem, long duration) {
        this.afterMediaItem = afterMediaItem;
        this.beforeMediaItem = beforeMediaItem;
        this.duration = duration;
    }

    public Media getAfterMediaItem() {
        return afterMediaItem;
    }

    public void setAfterMediaItem(Media afterMediaItem) {
        this.afterMediaItem = afterMediaItem;
    }

    public Media getBeforeMediaItem() {
        return beforeMediaItem;
    }

    public void setBeforeMediaItem(Media beforeMediaItem) {
        this.beforeMediaItem = beforeMediaItem;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
