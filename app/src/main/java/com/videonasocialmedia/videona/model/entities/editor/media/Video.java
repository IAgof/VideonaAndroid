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

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

import java.io.File;
import java.util.ArrayList;

/**
 * A media video item that represents a file (or part of a file) that can be used in project video
 * track.
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Video extends Media {

    public static String VIDEO_PATH;

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String mediaPath, long fileStartTime,
                 long duration, ArrayList<User> authors, License license) {
        super(identifier, iconPath, mediaPath, fileStartTime, duration, authors, license);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String selectedIconPath, String title,
                 String mediaPath, long fileStartTime, long duration, Transition opening,
                 Transition ending, MediaMetadata metadata, ArrayList<User> authors,
                 License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration,
                opening, ending, metadata, authors, license);
    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     * //TODO no pides nada vero.  xD
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String mediaPath, long fileStartTime) {
        super(null, null, mediaPath, fileStartTime, 0, null, null);
    }

}
