/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.usecase;

import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.repository.EffectRepository;
import com.videonasocialmedia.videona.effects.repository.local.EffectLocalSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<Effect> getShaderEffectsList() {
        EffectRepository effectRepository = new EffectLocalSource();
        return effectRepository.getShaderEffectList();
    }

    public static List<Effect> getOverlayEffectsList() {
        EffectRepository effectRepository = new EffectLocalSource();
        List<Effect> castOverlayEffect = new ArrayList<>();
        for(Effect effect: effectRepository.getOverlayEffectList()){
            castOverlayEffect.add(effect);
        }
        return castOverlayEffect;
    }

    public static void discoverOverlayEffects(){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.discoverOverlayEffect();
    }

    public static void unlockLoggedOverlayEffects(){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.unlockLoggedOverlayEffects();
    }

    public static void lockLoggedOverlayEffects(){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.lockLoggedOverlayEffects();
    }

    public static void addPromocodeOverlayEffect(){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.addPromocodeOverlayEffect();
    }
}
