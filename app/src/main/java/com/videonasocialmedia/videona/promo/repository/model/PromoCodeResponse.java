/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.model;

/**
 *
 */
public class PromoCodeResponse {
    private final boolean valid_code;
    private final String campaign;
    private String error = null;

    public PromoCodeResponse(boolean validcode, String campaign, String error) {
        this.valid_code = validcode;
        this.campaign = campaign;
    }

    public boolean isValidCode() {
        return valid_code;
    }

    public String getCampaign() {
        return campaign;
    }

    public String getError() {
        return error;
    }
}
