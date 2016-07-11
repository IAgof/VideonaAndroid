/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain.model;

import java.util.Date;

/**
 *
 */
public class Promo {

    private final String campaign;
    private final Date activationDate;
    private boolean activated;
    private boolean expired;

    public Promo(boolean activated, boolean expired, String campaign, Date activationDate) {
        this.activated = activated;
        this.expired = expired;
        this.campaign = campaign;
        this.activationDate = activationDate;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }


}
