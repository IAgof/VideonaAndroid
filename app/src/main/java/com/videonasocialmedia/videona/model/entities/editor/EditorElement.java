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
     * Unique identifier for the element in the current project.
     */
    protected String identifier;

    /**
     * Path to icon. Cannot be null.
     */
    protected String iconPath;

    /**
     * Path to icon selected. If null use iconPath
     */
    protected String selectedIconPath;

    /**
     *
     * @param identifier - Unique identifier of element in the current project.
     * @param iconPath - Path to a resource that allows represent the element in the view.
     */
    protected EditorElement(String identifier, String iconPath) {
        this.identifier = identifier;
        this.iconPath = iconPath;
        this.selectedIconPath = null;
    }

    /**
     * Constructor of minimum number of parameters.
     *
     * @param identifier - Unique identifier of element in the current project.
     * @param iconPath - path to a resource to allow represent the element in the view.
     * @param selectedIconPath - if not null used as icon when something interact with the element.
     *                         If null it will be used the iconPath as default.
     */
    protected EditorElement(String identifier, String iconPath, String selectedIconPath) {
        this.iconPath = iconPath;
        this.selectedIconPath = selectedIconPath;
        this.identifier = identifier;
    }

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
