/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.model;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alvaro on 20/07/16.
 */
public class RealmEffect extends RealmObject {
  @PrimaryKey
  public String uuid;
  public String effectType;
  public String identifier;
  public String name;
  public int coverIconId;
  public int iconId;
  public int resourceId;
  public String permissionType;
  public boolean activated;

  public RealmEffect(){

  }

  public RealmEffect(String uuid, String identifier, String name, int coverIconId, int iconId,
                     int resourceId, String effectType, boolean activated, String permissionType) {
    this.uuid = uuid;
    this.identifier = identifier;
    this.name = name;
    this.coverIconId = coverIconId;
    this.iconId = iconId;
    this.resourceId = resourceId;
    this.effectType = effectType;
    this.activated = activated;
    this.permissionType = permissionType;

  }

  public String getPermissionType() {
    return permissionType;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }
}
