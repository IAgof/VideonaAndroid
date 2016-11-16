/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.model;

import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;


/**
 * Created by alvaro on 16/11/16.
 */

public class OverlayEffectToRealmEffectMapper implements Mapper<OverlayEffect, RealmEffect> {
  @Override
  public RealmEffect map(OverlayEffect overlayEffect) {

    RealmEffect realmEffect = new RealmEffect(overlayEffect.getUuid(),overlayEffect.getIdentifier(),
        overlayEffect.getName(), overlayEffect.getCoverIconId(),overlayEffect.getIconId(),
        overlayEffect.getResourceId(),overlayEffect.getEffectType(),overlayEffect.getActivated(),
        overlayEffect.getPermissionType());

    return realmEffect;
  }
}
