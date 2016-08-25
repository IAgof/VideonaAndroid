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

import android.graphics.BitmapFactory;
import android.media.MediaMetadata;

import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

import java.io.File;
import java.util.ArrayList;

/**
 * A media image item that represents a file that can be used in project video track.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Image extends Media {

    public static final int DEFAULT_IMAGE_DURATION = 3;
    public static String IMAGE_PATH = "";


    /**
     * Constructor of minimum number of parameters. Default constructor.
     * <p/>
     * An image always starts at the beginning of the file and has a default duration fixed for
     * recent created image objects. However this duration could be changed during the edition
     * process, but the startTime never could be changed.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Image(String identifier, String iconPath, String mediaPath, ArrayList<User> authors,
                 License license) {
        super(identifier, iconPath, mediaPath, 0, Image.DEFAULT_IMAGE_DURATION, authors, license);
    }

    /**
     * Lazy constructor. Creating a image media item from the mediaPath only.
     *
     * @param mediaPath
     */
    public Image(String mediaPath) {
        super(null, null, mediaPath, 0, Image.DEFAULT_IMAGE_DURATION, null, null);
        //check if the mediapath is an image.

        //get the iconpath

        //resolve lincese by default.
        this.setLicense(new License(License.CC40_NAME, License.CC40_TEXT));

    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Image(String identifier, String iconPath, String selectedIconPath, String title, String
            mediaPath, int duration, Transition opening, Transition ending,
                 MediaMetadata metadata, ArrayList<User> authors, License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, 0, duration,
                opening, ending, metadata, authors, license);
    }

    @Override
    public int getStartTime() {
        return 0;
    }

    @Override
    public void setStartTime(int startTime) {
        //
    }

    //utils
    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }
}
