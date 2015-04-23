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
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * A filter is a type of effect that applies a distortion to the video image.
 * Created by dfa on 7/4/15.
 */
public class Filter extends Effect {

    /**
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Filter(String identifier, String iconPath, String type, long startTime, long duration, License license, User author) {
        super(identifier, iconPath, type, startTime, duration, license, author);
    }

    /**
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Filter(String iconPath, String selectedIconPath, String identifier, String type,
                  long startTime, long duration, int layer, User author, License license) {
        super(iconPath, selectedIconPath, identifier, type, startTime, duration, layer, author,
                license);
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
