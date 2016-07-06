/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.model;

import com.google.gson.Gson;

/**
 *
 */
public class RegisterRequest {
    private final String username;
    private final String password;

    public RegisterRequest(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

