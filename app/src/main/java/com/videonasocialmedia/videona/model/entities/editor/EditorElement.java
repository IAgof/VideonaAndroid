/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

import android.net.Uri;

import java.net.URI;

/**
 * Created by jca on 25/3/15.
 */
public abstract class EditorElement {

    protected int iconResourceId;
    protected String name;

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }


}
