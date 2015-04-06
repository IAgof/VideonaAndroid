/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

/**
 * Created by jca on 25/3/15.
 */
public abstract class EditorElement {

    protected String iconPath;
    protected String name;

    // amm
    protected int iconResourceId;

    public int getIconResourceId() {
        return iconResourceId;
    }

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


}
