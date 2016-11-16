/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.model;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;

import java.util.UUID;

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
    protected final String effectType;
    protected final boolean activated;
    protected final String permissionType;

    private String uuid = UUID.randomUUID().toString();

    /**
     * Constructor.
     *  @param identifier          - Identifier of the effect
     * @param name                - Name of the effect
     * @param iconPath    - Path to the icon resource
     * @param activated
     */
    public Effect(String identifier, String name, String iconPath, String effectType, boolean activated) {
        this.identifier = identifier;
        this.name = name;
        this.iconPath = iconPath;
        this.activated = activated;
        this.iconId = -1;
        this.coverIconId = -1;
        this.effectType = effectType;
        this.permissionType = PermissionType.ALL.name();
    }

    /**
     * Constructor.
     *  @param identifier        - Identifier of the effect
     * @param name              - Name of the effect
     * @param iconId    - Path to the icon resource
     * @param activated
     */
    public Effect(String identifier, String name, int coverIconId, int iconId, String effectType, boolean activated) {
        this.identifier = identifier;
        this.name = name;
        this.activated = activated;
        this.iconPath = null;
        this.coverIconId = coverIconId;
        this.iconId = iconId;
        this.effectType = effectType;
        this.permissionType = PermissionType.ALL.name();
    }

    public Effect(String identifier, String name, int iconId, String effectType, boolean activated) {
        this.identifier = identifier;
        this.name = name;
        this.activated = activated;
        this.iconPath = null;
        this.coverIconId = -1;
        this.iconId = iconId;
        this.effectType = effectType;
        this.permissionType = PermissionType.ALL.name();
    }

    public Effect(String identifier, String name, int coverIconId, int iconId, String effectType,
                  boolean activated, String permissionType) {
        this.identifier = identifier;
        this.name = name;
        this.activated = activated;
        this.iconPath = null;
        this.coverIconId = coverIconId;
        this.iconId = iconId;
        this.effectType = effectType;
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

    public String getEffectType() { return effectType; }

    public boolean getActivated(){
        return activated;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public String getUuid() {
        return uuid;
    }
}
