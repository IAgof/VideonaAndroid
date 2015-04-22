/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */

package com.videonasocialmedia.videona.model.entities.editor.effects;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * A filter is a type of effect that applies a distortion to the video image.
 * Created by dfa on 7/4/15.
 */
public class Filter extends Effect {

    /**
     * Default constructor. Called when owner is a videona user.
     *
     * @param filterType - Unique filter identifier.
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Filter(String filterType, int layer, long startTime, long duration, User author) {
        super(filterType, layer, startTime, duration, author);
    }

    /**
     * Constructor called when owner is not a videona user.
     *
     * @param filterType - Unique filter identifier.
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Filter(String filterType, int layer, long startTime, long duration, String authorName) {
        super(filterType, layer, startTime, duration, authorName);
    }

    //application methods

    /**
     * TODO
     * @param target - the media file to be modified by the effect.
     */
    @Override
    public void doTheMagic(Media target) {

    }

    /**
     * TODO
     */
    @Override
    public void preview() {

    }
}
