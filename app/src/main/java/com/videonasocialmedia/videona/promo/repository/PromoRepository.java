/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository;

import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.local.PromoRepositoryCallBack;

/**
 *
 */
public interface PromoRepository {
    void getAllPromos(PromoRepositoryCallBack callBack);

    void getActivePromos(PromoRepositoryCallBack callBack);

    void getInactivePromos(PromoRepositoryCallBack callBack);

    void getPromo(String campaign, PromoRepositoryCallBack callBack);

    void setPromo(Promo promo, PromoRepositoryCallBack callBack);

    void deletePromo(final Promo promo, final PromoRepositoryCallBack callBack);
}
