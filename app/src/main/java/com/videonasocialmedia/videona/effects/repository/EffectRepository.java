/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository;

import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by alvaro on 20/07/16.
 */
public interface EffectRepository {

    RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getShaderEffectList();

    void addShaderEffectList();

    void discoverShaderEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect);

    RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getOverlayEffectList();

    void addOverlayEffectList();

    void discoverOverlayEffect();

    void unlockLoggedOverlayEffects();

    void lockLoggedOverlayEffects();
}
