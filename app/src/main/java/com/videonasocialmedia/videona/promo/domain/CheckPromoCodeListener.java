/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain;

/**
 *
 */
public interface CheckPromoCodeListener {
    void onSuccess(String campaign);

    void onError(Causes cause);

    enum Causes {
        INVALID, UNAUTHORIZED, REDEEMED, EXPIRED, UNKNOWN
    }
}
