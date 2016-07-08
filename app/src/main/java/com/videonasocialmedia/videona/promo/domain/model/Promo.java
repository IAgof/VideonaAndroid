/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain.model;

/**
 *
 */
public class Promo {

    private final boolean activated;
    private final String campaign;

    public Promo(String campaign, boolean activated) {
        this.activated = activated;
        this.campaign = campaign;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getCampaign() {
        return campaign;
    }

}
