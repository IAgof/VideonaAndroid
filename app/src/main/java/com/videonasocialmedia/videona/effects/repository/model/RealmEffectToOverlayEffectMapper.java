/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.model;

import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;

import io.realm.RealmList;

/**
 * Created by alvaro on 16/11/16.
 */

public class RealmEffectToOverlayEffectMapper implements Mapper<RealmEffect, OverlayEffect> {

  @Override
  public OverlayEffect map(RealmEffect realmEffect) {

    OverlayEffect effect = new OverlayEffect(realmEffect.identifier, realmEffect.name,
        realmEffect.coverIconId, realmEffect.iconId, realmEffect.resourceId,
        realmEffect.effectType,realmEffect.permissionType, realmEffect.activated);
    return effect;
  }
}
