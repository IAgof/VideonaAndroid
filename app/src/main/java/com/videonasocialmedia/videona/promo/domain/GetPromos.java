/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain;

import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.PromoRepository;
import com.videonasocialmedia.videona.promo.repository.local.PromoRepositoryCallBack;
import com.videonasocialmedia.videona.promo.repository.local.PromosLocalSource;

import java.util.List;

/**
 *
 */
public class GetPromos {

    PromoRepository repo = new PromosLocalSource();

    public void getInactivePromos(GetPromosListener listener) {
        repo.getInactivePromos(new Listener(listener));
    }

    public void getActivePromos(GetPromosListener listener) {
        repo.getActivePromos(new Listener(listener));
    }


    public void getAllPromos(GetPromosListener listener) {
        repo.getAllPromos(new Listener(listener));
    }


    public void getPromo(String campaign, GetPromosListener listener) {
        repo.getPromo(campaign, new Listener(listener));
    }

    private class Listener implements PromoRepositoryCallBack {

        final GetPromosListener listener;

        public Listener(GetPromosListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPromosRetrieved(List<Promo> promos) {
            onPromosRetrieved(promos);
        }

        @Override
        public void onPromoSetted() {

        }

        @Override
        public void onPromoDeleted() {

        }

        @Override
        public void onError(Throwable error) {
            listener.onError();
        }
    }
}
