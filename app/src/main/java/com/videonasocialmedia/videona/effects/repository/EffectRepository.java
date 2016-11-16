/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository;

import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;
import com.videonasocialmedia.videona.effects.domain.model.ShaderEffect;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by alvaro on 20/07/16.
 */
public interface EffectRepository {

    List<Effect> getShaderEffectList();

    List<OverlayEffect> getOverlayEffectList();

    void addOverlayEffectList();

    void discoverOverlayEffect();

    void unlockLoggedOverlayEffects();

    void lockLoggedOverlayEffects();

    void addPromocodeOverlayEffect();
}
