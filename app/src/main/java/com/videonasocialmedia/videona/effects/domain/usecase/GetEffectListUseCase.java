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

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<Effect> getShaderEffectsList() {

        EffectRepository effectRepository = new EffectLocalSource();
        //RealmResults realmResults = effectRepository.getShaderEffectList();

        return effectRepository.getShaderEffectList();
    }

    public static void updateShaderEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.discoverShaderEffect(effect);
    }

    public static RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getOverlayEffectsList() {

        EffectRepository effectRepository = new EffectLocalSource();
        RealmResults realmResults = effectRepository.getOverlayEffectList();

        if(realmResults.size() == 0){
            effectRepository.addOverlayEffectList();
        }


        return effectRepository.getOverlayEffectList();
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
}
