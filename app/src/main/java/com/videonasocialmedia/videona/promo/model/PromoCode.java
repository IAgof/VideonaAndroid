/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.model;

import com.google.gson.Gson;

/**
 *
 */
public class PromoCode {
    private final String code;

    public PromoCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
