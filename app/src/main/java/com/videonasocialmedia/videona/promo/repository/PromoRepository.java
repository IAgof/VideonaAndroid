/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository;

import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.local.PromoRepositoryCallBack;

import java.util.List;

/**
 *
 */
public interface PromoRepository {
    List<Promo> getAllPromos();

    List<Promo> getActivePromos();

    List<Promo> getInactivePromos();

    List<Promo> getPromosByCampaign(String campaign);

    void setPromo(Promo promo, PromoRepositoryCallBack callBack);

    void deletePromo(final Promo promo, final PromoRepositoryCallBack callBack);
}
