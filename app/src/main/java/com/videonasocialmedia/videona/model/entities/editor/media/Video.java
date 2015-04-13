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
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;
import java.util.ArrayList;

/**
 * Represents a media video item.
 * Created by jca on 30/3/15.
 */
public class Video extends Media {

    public Video(String title, String mediaPath, long mediaStartTime, MediaMetadata metadata,
                 long duration, ArrayList<User> authors, ArrayList<String> authorsNames,
                 License license) {
        super(title, mediaPath, mediaStartTime, metadata, duration, authors, authorsNames, license);
    }
}
