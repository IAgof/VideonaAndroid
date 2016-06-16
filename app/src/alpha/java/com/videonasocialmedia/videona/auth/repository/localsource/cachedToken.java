/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.localsource;


import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.domain.model.Token;

/**
 *
 */
public class CachedToken {

    private final SharedPreferences.Editor editor;
    private SharedPreferences authPreference;

    public CachedToken() {
        authPreference = VideonaApplication.getAppContext()
                .getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        editor = authPreference.edit();
    }

    public boolean hasToken() {
        return authPreference.getBoolean("HAS_TOKEN", false);
    }

    public Token getToken() {
        String token = authPreference.getString("AUTH_TOKEN", null);
        return new Token(token);
    }

    public void setToken(Token token) {
        editor.putString("AUTH_TOKEN", token.getToken());
        editor.putBoolean("HAS_TOKEN", true);
        editor.apply();
    }

    public void deleteToken() {
        editor.putString("AUTH_TOKEN", null);
        editor.putBoolean("HAS_TOKEN", false);
        editor.apply();
    }

}
