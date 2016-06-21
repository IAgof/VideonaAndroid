/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.model.entities;

/**
 *
 */
public abstract class Effect {

    /**
     * Identifier of the effect. Cannot be null.
     */
    protected final String identifier;
    /**
     * Name of the effect. Cannot be null.
     */
    protected final String name;
    /**
     * Path to icon resource. Cannot be null.
     */
    protected final String iconPath;
    protected final int iconId;
    protected final String type;

    /**
     * Constructor.
     *
     * @param identifier          - Identifier of the effect
     * @param name                - Name of the effect
     * @param iconPath    - Path to the icon resource
     */
    public Effect(String identifier, String name, String iconPath, String type) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = iconPath;
        this.iconId = -1;
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param identifier        - Identifier of the effect
     * @param name              - Name of the effect
     * @param iconId    - Path to the icon resource
     */
    public Effect(String identifier, String name, int iconId, String type) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = null;
        this.iconId = iconId;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public int getIconId() {
        return iconId;
    }

    public String getType() { return type; }


}
