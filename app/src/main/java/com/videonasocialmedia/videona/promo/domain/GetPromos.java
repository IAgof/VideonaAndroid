/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain;

import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.PromoRepository;
import com.videonasocialmedia.videona.promo.repository.local.PromosLocalSource;

import java.util.List;

/**
 *
 */
public class GetPromos {

    PromoRepository repo = new PromosLocalSource();

    public void getInactivePromos(GetPromosListener listener) {
        List<Promo> promos = repo.getInactivePromos();
        listener.onPromosRetrieved(promos);
    }

    public void getActivePromos(GetPromosListener listener) {
        List<Promo> promos = repo.getActivePromos();
        listener.onPromosRetrieved(promos);
    }


    public void getAllPromos(GetPromosListener listener) {
        List<Promo> promos = repo.getAllPromos();
        listener.onPromosRetrieved(promos);
    }


    public void getPromosByCampaign(String campaign, GetPromosListener listener) {
        List<Promo> promos = repo.getPromosByCampaign(campaign);
        listener.onPromosRetrieved(promos);
    }

}
