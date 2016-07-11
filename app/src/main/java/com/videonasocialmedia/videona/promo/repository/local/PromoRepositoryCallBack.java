/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.local;

import com.videonasocialmedia.videona.promo.domain.model.Promo;

import java.util.List;

/**
 *
 */
public interface PromoRepositoryCallBack {
    void onPromosRetrieved(List<Promo> promos);

    void onPromoSetted();

    void onPromoDeleted();

    void onError(Throwable error);
}
