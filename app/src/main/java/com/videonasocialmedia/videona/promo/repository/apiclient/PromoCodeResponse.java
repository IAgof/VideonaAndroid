/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.apiclient;

/**
 *
 */
public class PromoCodeResponse {
    private final boolean validCode;

    public PromoCodeResponse(boolean validCode) {
        this.validCode = validCode;
    }

    public boolean isValidCode() {
        return validCode;
    }
}
