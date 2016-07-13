/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.PromoRepository;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 *
 */
public class PromosLocalSource implements PromoRepository {

    @Override
    public List<Promo> getAllPromos() {
        RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> query
                = getPromoRealmQuery();
        RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result =
                query.findAll();
        List<Promo> promos = mapPromos(result);
        return promos;
    }

    @Override
    public List<Promo> getActivePromos() {
        RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> query
                = getPromoRealmQuery();
        query.equalTo("activated", true);
        RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result =
                query.findAll();
        List<Promo> promos = mapPromos(result);
        return promos;
    }

    @Override
    public List<Promo> getInactivePromos() {
        RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> query
                = getPromoRealmQuery();
        query.equalTo("activated", false);
        RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result =
                query.findAll();
        List<Promo> promos = mapPromos(result);
        return promos;
    }

    @Override
    public List<Promo> getPromosByCampaign(String campaign) {
        RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> query
                = getPromoRealmQuery();
        query.equalTo("campaign", campaign);
        RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result =
                query.findAll();
        List<Promo> promos = mapPromos(result);
        return promos;
    }

    @Override
    public void setPromo(final Promo promo, final PromoRepositoryCallBack callBack) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                com.videonasocialmedia.videona.promo.repository.model.Promo realmPromo =
                        realm.createObject(com.videonasocialmedia.videona.promo.repository.model.Promo.class);
                realmPromo.setActivated(promo.isActivated());
                realmPromo.setCampaign(promo.getCampaign());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(PromosLocalSource.class.getCanonicalName(), "writing to database successfully");
                if (callBack != null) callBack.onPromoSetted();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(PromosLocalSource.class.getCanonicalName(), "error writing to database", error);
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        });
    }

    public void deletePromo(final Promo promo, final PromoRepositoryCallBack callBack) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> query
                        = getPromoRealmQuery();
                query.equalTo("campaign", promo.getCampaign());
                RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result =
                        query.findAll();
                result.deleteFirstFromRealm();
            }
        });
    }

    @NonNull
    private RealmQuery<com.videonasocialmedia.videona.promo.repository.model.Promo> getPromoRealmQuery() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(com.videonasocialmedia.videona.promo.repository.model.Promo.class);
    }

    private List<Promo> mapPromos(RealmResults<com.videonasocialmedia.videona.promo.repository.model.Promo> result) {
        List<Promo> promos = new ArrayList<>();
        for (com.videonasocialmedia.videona.promo.repository.model.Promo realmPromo : result) {
            promos.add(new Promo(realmPromo.getCampaign(), realmPromo.isActivated()));
        }
        return promos;
    }
}
