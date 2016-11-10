/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.local;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.EffectType;
import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;
import com.videonasocialmedia.videona.effects.domain.model.ShaderEffect;
import com.videonasocialmedia.videona.effects.repository.EffectRepository;
import com.videonasocialmedia.videona.effects.repository.apiclient.EffectProvider;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by alvaro on 20/07/16.
 */
public class EffectLocalSource implements EffectRepository {


    @Override
    public RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getShaderEffectList() {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<com.videonasocialmedia.videona.effects.repository.model.Effect> query = realm.where(com.videonasocialmedia.videona.effects.repository.model.Effect.class);
        if (query.contains("typeEffect", EffectType.SHADER.toString()).findAll().size() == 0) {
            addShaderEffectList();
        }

        RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> result =
                query.contains("typeEffect", EffectType.SHADER.toString()).findAll();

        //return mapEffectListToDomainModel(result);
        return result;
    }

    @NonNull
    private List<Effect> mapEffectListToDomainModel(RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> result) {
        List<Effect> finalResult = new ArrayList<>();
        for (com.videonasocialmedia.videona.effects.repository.model.Effect currentEffect : result) {
            Effect effect = new ShaderEffect(currentEffect.getIdentifier(), currentEffect.getName(),
                    currentEffect.getIconId(),
                    currentEffect.getResourceId(), currentEffect.getTypeEffect());
            finalResult.add(effect);
        }
        return finalResult;
    }

    public void addShaderEffect(ShaderEffect shaderEffect) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        com.videonasocialmedia.videona.effects.repository.model.Effect realmShaderEffect = realm.createObject(com.videonasocialmedia.videona.effects.repository.model.Effect.class);
        realmShaderEffect.setTypeEffect(EffectType.SHADER.toString());
        realmShaderEffect.setIdentifier(shaderEffect.getIdentifier());
        realmShaderEffect.setName(shaderEffect.getName());
        realmShaderEffect.setIconId(shaderEffect.getIconId());
        realmShaderEffect.setPermissionType(shaderEffect.getPermissionType().toString());
        realmShaderEffect.setResourceId(shaderEffect.getResourceId());
        realmShaderEffect.setAnalyticsType(shaderEffect.getType());
        realmShaderEffect.setActivated(true);

        realm.commitTransaction();

    }

    @Override
    public void addShaderEffectList() {

        for (Effect effect : EffectProvider.getShaderEffectList()) {
            addShaderEffect((ShaderEffect) effect);
        }
    }

    @Override
    public void discoverShaderEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        effect.setActivated(true);

        realm.commitTransaction();
    }

    @Override
    public RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> getOverlayEffectList() {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<com.videonasocialmedia.videona.effects.repository.model.Effect> query = realm.where(com.videonasocialmedia.videona.effects.repository.model.Effect.class);
        if (query.contains("typeEffect", EffectType.OVERLAY.toString()).findAll().size() == 0) {
            addOverlayEffectList();
        }

        return query.contains("typeEffect", EffectType.OVERLAY.toString()).findAll();

    }

    @Override
    public void addOverlayEffectList() {

        for (Effect effect : EffectProvider.getOverlayFilterList()) {
            addOverlayEffect((OverlayEffect) effect);
        }
    }

    @Override
    public void discoverOverlayEffect() {

        RealmResults<com.videonasocialmedia.videona.effects.repository.model.Effect> result = getOverlayEffectList();

        RealmQuery<com.videonasocialmedia.videona.effects.repository.model.Effect> effect = result.where().contains("permissionType", PermissionType.ALL.toString());
        for (int i = 0; i < effect.findAll().size(); i++) {
            discoverOverlayEffect(effect.findAll().get(i));
        }

    }

    public void discoverOverlayEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        effect.setActivated(true);

        realm.commitTransaction();
    }

    public void addOverlayEffect(OverlayEffect overlayEffect) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        com.videonasocialmedia.videona.effects.repository.model.Effect realmOverlayEffect = realm.createObject(com.videonasocialmedia.videona.effects.repository.model.Effect.class);
        realmOverlayEffect.setTypeEffect(EffectType.OVERLAY.toString());
        realmOverlayEffect.setIdentifier(overlayEffect.getIdentifier());
        realmOverlayEffect.setName(overlayEffect.getName());
        realmOverlayEffect.setCoverIconId(overlayEffect.getCoverIconId());
        realmOverlayEffect.setIconId(overlayEffect.getIconId());
        realmOverlayEffect.setPermissionType(overlayEffect.getPermissionType().toString());
        realmOverlayEffect.setResourceId(overlayEffect.getResourceId());
        realmOverlayEffect.setAnalyticsType(overlayEffect.getType());
        realmOverlayEffect.setActivated(false);

        realm.commitTransaction();

    }

    @Override
    public void unlockLoggedOverlayEffects() {

        for (com.videonasocialmedia.videona.effects.repository.model.Effect effect : getOverlayEffectList().where().findAll()) {
            if (effect.getPermissionType().toString().compareTo(PermissionType.LOGGED_IN.toString()) == 0) {
                discoverOverlayEffect(effect);
            }
        }

    }

    @Override
    public void lockLoggedOverlayEffects() {

        for (com.videonasocialmedia.videona.effects.repository.model.Effect effect : getOverlayEffectList().where().findAll()) {
            if (effect.getPermissionType().toString().compareTo(PermissionType.LOGGED_IN.toString()) == 0) {
                hideOverlayEffect(effect);
            }
        }
    }

    public void hideOverlayEffect(com.videonasocialmedia.videona.effects.repository.model.Effect effect) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        effect.setActivated(false);

        realm.commitTransaction();
    }

}
