/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.usecase;

import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.EffectType;
import com.videonasocialmedia.videona.effects.repository.EffectRepository;
import com.videonasocialmedia.videona.effects.repository.apiclient.EffectProvider;
import com.videonasocialmedia.videona.effects.repository.local.EffectLocalSource;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getShaderEffectsList() {

        EffectRepository effectRepository = new EffectLocalSource();
        //RealmResults realmResults = effectRepository.getShaderEffectList();

        return effectRepository.getShaderEffectList();
    }

    public static void updateShaderEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.updateShaderEffect(effect);
    }

    public static RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getOverlayEffectsList() {

        EffectRepository effectRepository = new EffectLocalSource();
        RealmResults realmResults = effectRepository.getOverlayEffectList();

        if(realmResults.size() == 0){
            effectRepository.addOverlayEffectList();
        }


        return effectRepository.getOverlayEffectList();
    }

    public static void updateOverlayEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect){
        EffectRepository effectRepository = new EffectLocalSource();
        effectRepository.updateOverlayEffect(effect);
    }
}
