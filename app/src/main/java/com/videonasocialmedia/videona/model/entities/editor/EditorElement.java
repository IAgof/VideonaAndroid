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

package com.videonasocialmedia.videona.model.entities.editor;

/**
 * Defines any element that can be rendered on the editor.
 *
 * Created by jca on 25/3/15.
 */
public abstract class EditorElement {

    /**
     * Path to icon
     * TODO esto no debería resolverlo la vista?
     */
    protected String iconPath;

    /**
     * Path to icon selected.
     * TODO esto no debería resolverlo la vista?
     */
    protected String selectedIconPath;

    /**
     * Unique identifier for the element in the current project.
     */
    protected String identifier;

    public String getIconPath() {
        return iconPath;
    }
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    public String getSelectedIconPath() {
        return selectedIconPath;
    }
    public void setSelectedIconPath(String selectedIconPath) {
        this.selectedIconPath = selectedIconPath;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
