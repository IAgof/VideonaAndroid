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

package com.videonasocialmedia.videona.model.entities.editor.effects;

import com.videonasocialmedia.videona.model.entities.editor.EditorElement;

import java.net.URI;

public abstract class Effect extends EditorElement {

    /**
     * Duration of the effect
     */
    protected long duration;
    /**
     * The moment the effect is applied since start of the track
     */
    protected long startTime;

    protected Effect(long duration, long startTime) {
        this.duration = duration;
        this.startTime = startTime;
    }
}
