/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.User.domain.model;

import com.videonasocialmedia.videona.promo.repository.model.Promo;

import java.util.List;

/**
 *
 */
public class User {
    private String email;
    private String userName;
    private List<Promo> promos;
}
