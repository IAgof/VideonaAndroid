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
 * A media image item that represents a file that can be used in project video track.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Image extends Media {

    public static final long DEFAULT_IMAGE_DURATION = 3;


    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * An image always starts at the beginning of the file and has a default duration fixed for
     * recent created image objects. However this duration could be changed during the edition
     * process, but the fileStartTime never could be changed.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Image(String identifier, String iconPath, String mediaPath, ArrayList<User> authors,
                 License license) {
        super(identifier, iconPath, mediaPath, 0, Image.DEFAULT_IMAGE_DURATION, authors, license);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Image(String identifier, String iconPath, String selectedIconPath, String title, String
            mediaPath, long duration, Transition opening, Transition ending,
                 MediaMetadata metadata, ArrayList<User> authors, License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, 0, duration,
                opening, ending, metadata, authors, license);
    }

    @Override
    public long getFileStartTime() {
        return 0;
    }
    @Override
    public void setFileStartTime(long fileStartTime) {
        //
    }
}
