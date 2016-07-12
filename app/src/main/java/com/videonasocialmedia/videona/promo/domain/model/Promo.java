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

    private final String campaign;
    // private final Date activationDate;
    private boolean activated;
//    private boolean expired;


    public Promo(String campaign, boolean activated) {
        this.campaign = campaign;
        this.activated = activated;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getCampaign() {
        return campaign;
    }

//    public boolean isExpired() {
//        return expired;
//    }
//
//    public void setExpired(boolean expired) {
//        this.expired = expired;
//    }


}
