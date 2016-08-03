/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.model;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;

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
    protected final int coverIconId;
    protected final String type;

    protected final PermissionType permissionType;

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
        this.coverIconId = -1;
        this.type = type;
        this.permissionType = PermissionType.ALL;
    }

    /**
     * Constructor.
     *
     * @param identifier        - Identifier of the effect
     * @param name              - Name of the effect
     * @param iconId    - Path to the icon resource
     */
    public Effect(String identifier, String name, int coverIconId, int iconId, String type) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = null;
        this.coverIconId = coverIconId;
        this.iconId = iconId;
        this.type = type;
        this.permissionType = PermissionType.ALL;
    }

    public Effect(String identifier, String name, int iconId, String type) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = null;
        this.coverIconId = -1;
        this.iconId = iconId;
        this.type = type;
        this.permissionType = PermissionType.ALL;
    }

    public Effect(String identifier, String name, int coverIconId, int iconId, String type,
                  PermissionType permissionType) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = null;
        this.coverIconId = coverIconId;
        this.iconId = iconId;
        this.type = type;
        this.permissionType = permissionType;
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

    public int getCoverIconId(){
        return coverIconId;
    }

    public String getType() { return type; }

    public PermissionType getPermissionType() {
        return permissionType;
    }
}
