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
    private final boolean validcode;
    private final String campaign;

    public PromoCodeResponse(boolean validcode, String campaign) {
        this.validcode = validcode;
        this.campaign = campaign;
    }

    public boolean isValidCode() {
        return validcode;
    }

    public String getCampaign() {
        return campaign;
    }
}
