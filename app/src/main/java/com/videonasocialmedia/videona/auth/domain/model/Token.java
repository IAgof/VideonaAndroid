/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.domain.model;

/**
 *
 */
public class Token {
    private final String access_token;

    public Token(String token) {
        this.access_token = token;
    }

    public String getToken() {
        return access_token;
    }
}
