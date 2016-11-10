/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.model;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alvaro on 20/07/16.
 */
public class Effect extends RealmObject{

    @Required
    private String typeEffect;

    @Required
    private String identifier;
    @Required
    private String name;
    private int coverIconId;
    //@Required
    private int iconId;
    //@Required
    private int resourceId;
    @Required
    private String analyticsType;
    @Required
    private String permissionType;

    private boolean activated;

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getCoverIconId() {
        return coverIconId;
    }

    public void setCoverIconId(int coverIconId) {
        this.coverIconId = coverIconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getAnalyticsType() {
        return analyticsType;
    }

    public void setAnalyticsType(String type) {
        this.analyticsType = type;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getTypeEffect() {
        return typeEffect;
    }

    public void setTypeEffect(String typeEffect) {
        this.typeEffect = typeEffect;
    }

}
