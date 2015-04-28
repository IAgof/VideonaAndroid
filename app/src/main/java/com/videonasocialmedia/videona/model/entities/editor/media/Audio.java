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
package com.videonasocialmedia.videona.model.entities.editor.media;

import android.media.MediaMetadata;

import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

import java.util.ArrayList;

/**
 * An audio media item that represents a file  (or part of a file) that can be used in project audio
 * track.
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Audio extends Media {

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Audio(String iconPath, String mediaPath, long fileStartTime, long duration, ArrayList<User> authors, License license) {
        super(iconPath, mediaPath, fileStartTime, duration, authors, license);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Audio(String iconPath, String selectedIconPath, String title, String mediaPath, long fileStartTime, long duration, Transition opening, Transition ending, MediaMetadata metadata, ArrayList<User> authors, License license) {
        super(iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration, opening, ending, metadata, authors, license);
    }
}