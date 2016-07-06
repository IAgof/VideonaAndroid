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
public final class CachedToken {

    private static final SharedPreferences authPreference = VideonaApplication.getAppContext()
            .getSharedPreferences("AUTH", Context.MODE_PRIVATE);

    private CachedToken() {

    }

    public static boolean hasToken() {
        return authPreference.getBoolean("HAS_TOKEN", false);
    }

    public static Token getToken() {
        String token = authPreference.getString("AUTH_TOKEN", null);
        return new Token(token);
    }

    public static void setToken(Token token) {
        SharedPreferences.Editor editor = authPreference.edit();
        editor.putString("AUTH_TOKEN", token.getToken());
        editor.putBoolean("HAS_TOKEN", true);
        editor.apply();
    }

    public static void deleteToken() {
        SharedPreferences.Editor editor = authPreference.edit();
        editor.putString("AUTH_TOKEN", null);
        editor.putBoolean("HAS_TOKEN", false);
        editor.apply();
    }
}
