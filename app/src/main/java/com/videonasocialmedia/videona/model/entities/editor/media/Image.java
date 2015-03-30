/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor.media;

import android.media.MediaMetadata;

import java.util.ArrayList;

/**
 *
 * Created by jca on 30/3/15.
 * @author  Juan Javier cabanas
 */
public class Image extends Media {

    public Image(String mediaPath, long duration, long startTime, String title, ArrayList<Integer> authors, MediaMetadata metadata) {
        super(mediaPath, duration, startTime, title, authors, metadata);
    }


    public Image(String mediaPath,  long startTime, String title, ArrayList<Integer> authors, MediaMetadata metadata) {
        super(mediaPath, 3, startTime, title, authors, metadata);
    }
}
