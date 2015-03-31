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

    protected String iconPath;
    protected String selectedIconPath;
    protected String name;

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSelectedIconPath() {
        return selectedIconPath;
    }

    public void setSelectedIconPath(String selectedIconPath) {
        this.selectedIconPath = selectedIconPath;
    }
}
