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
import com.videonasocialmedia.videona.effects.repository.model.OverlayEffectToRealmEffectMapper;
import com.videonasocialmedia.videona.effects.repository.model.RealmEffect;
import com.videonasocialmedia.videona.effects.repository.model.RealmEffectToOverlayEffectMapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by alvaro on 20/07/16.
 */
public class EffectLocalSource implements EffectRepository {

    private final RealmEffectToOverlayEffectMapper toOverlayEffectMapper;
    private final OverlayEffectToRealmEffectMapper toRealmEffectMapper;

    public EffectLocalSource(){
        toOverlayEffectMapper = new RealmEffectToOverlayEffectMapper();
        toRealmEffectMapper = new OverlayEffectToRealmEffectMapper();
    }

    @Override
    public List<Effect> getShaderEffectList() {

        return EffectProvider.getShaderEffectList();
    }

    @Override
    public List<OverlayEffect> getOverlayEffectList() {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<RealmEffect> query =
            realm.where(RealmEffect.class);
        if (query.contains("effectType", EffectType.OVERLAY.name()).findAll().size() == 0) {
            addOverlayEffectList();
        }

        RealmList<RealmEffect> finalList = new RealmList<RealmEffect>();


        RealmResults<RealmEffect> result =
            query.contains("effectType", EffectType.OVERLAY.name()).findAll();


        finalList.addAll(result.subList(0, result.size()));

        if(query.contains("name", "Wolder").findAll().size() != 0){
            finalList.move(finalList.size()-1, 1);
        }

        return mapOverlayEffectListToDomainModel(finalList);

    }

    @NonNull
    private List<OverlayEffect> mapOverlayEffectListToDomainModel(RealmList<RealmEffect> result) {
        List<OverlayEffect> finalResult = new ArrayList<>();
        for (RealmEffect currentRealmEffect : result) {
            OverlayEffect effect = toOverlayEffectMapper.map(currentRealmEffect);
          //  OverlayEffect effect = new OverlayEffect(currentRealmEffect.getIdentifier(), currentRealmEffect.getName(),
          //      currentRealmEffect.getCoverIconId(), currentRealmEffect.getIconId(), currentRealmEffect.getResourceId(),
          //      currentRealmEffect.getEffectType(),currentRealmEffect.getPermissionType(), currentRealmEffect.getActivated());
            finalResult.add(effect);
        }
        return finalResult;
    }

    @Override
    public void addOverlayEffectList() {

        for (Effect effect : EffectProvider.getOverlayFilterList()) {
            addOverlayEffect((OverlayEffect) effect);
        }
    }

    @Override
    public void discoverOverlayEffect() {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<RealmEffect> query =
            realm.where(RealmEffect.class);

        RealmResults<RealmEffect> result =
            query.contains("effectType", EffectType.OVERLAY.name()).findAll();

        RealmQuery<RealmEffect> effect =
            result.where().contains("permissionType", PermissionType.ALL.name());
        for (int i = 0; i < effect.findAll().size(); i++) {
            discoverOverlayEffect(effect.findAll().get(i));
        }

    }

    public void discoverOverlayEffect(final RealmEffect realmEffect) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmEffect.setActivated(true);
                realm.copyToRealmOrUpdate(realmEffect);
            }
        });

    }

    public void addOverlayEffect(final OverlayEffect overlayEffect) {

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmEffect realmEffect = toRealmEffectMapper.map(overlayEffect);
                realm.copyToRealm(realmEffect);
            }
        });
        realm.close();

      /*  Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmEffect realmOverlayEffect = realm.createObject(RealmEffect.class);

        realmOverlayEffect.setUuid(overlayEffect.getUuid());
        realmOverlayEffect.setIdentifier(overlayEffect.getIdentifier());
        realmOverlayEffect.setName(overlayEffect.getName());
        realmOverlayEffect.setCoverIconId(overlayEffect.getCoverIconId());
        realmOverlayEffect.setIconId(overlayEffect.getIconId());
        realmOverlayEffect.setResourceId(overlayEffect.getResourceId());
        realmOverlayEffect.setEffectType(overlayEffect.getEffectType());
        realmOverlayEffect.setActivated(overlayEffect.getActivated());
        realmOverlayEffect.setPermissionType(overlayEffect.getPermissionType());

        realm.commitTransaction();
        */
    }

    @Override
    public void unlockLoggedOverlayEffects() {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<RealmEffect> query =
            realm.where(RealmEffect.class);

        RealmResults<RealmEffect> result =
            query.contains("effectType", EffectType.OVERLAY.name()).findAll();

        for (RealmEffect realmEffect : result) {
            if (realmEffect.getPermissionType().compareTo(PermissionType.LOGGED_IN.name()) == 0) {
                discoverOverlayEffect(realmEffect);
            }
        }

    }

    @Override
    public void lockLoggedOverlayEffects() {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<RealmEffect> query =
            realm.where(RealmEffect.class);

        RealmResults<RealmEffect> result =
            query.contains("effectType", EffectType.OVERLAY.name()).findAll();

        for (RealmEffect realmEffect : result) {
            if (realmEffect.getPermissionType().compareTo(PermissionType.LOGGED_IN.name()) == 0) {
                hideOverlayEffect(realmEffect);
            }
        }
    }

    @Override
    public void addPromocodeOverlayEffect(){
       addOverlayEffect(EffectProvider.getPromocodeOverlayEffect());
    }

    public void hideOverlayEffect(RealmEffect realmEffect) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realmEffect.setActivated(false);

        realm.commitTransaction();
    }

}
