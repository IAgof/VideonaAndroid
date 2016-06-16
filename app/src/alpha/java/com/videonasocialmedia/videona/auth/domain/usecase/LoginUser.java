/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors: Juan Javier Cabanas
 */

package com.videonasocialmedia.videona.auth.domain.usecase;

import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthClient;
import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;

import java.io.IOException;

import retrofit2.Response;

/**
 *
 */
public class LoginUser {

    private CachedToken cachedToken;
    private AuthClient authClient;

    public void userIsLoggedIn() {
        cachedToken = new CachedToken();
        boolean hasToken = cachedToken.hasToken();
        if (hasToken) {
            // TODO(javi.cabanas): 16/6/16 implement callback
        } else {

        }
    }

    public void login(String email, String password) {
        authClient = new ServiceGenerator().generateService(AuthClient.class);
        try {
            Response<Token> response =
                    authClient.getAuthToken(new AuthTokenRequest(email, password)).execute();
            if (response.isSuccessful()) {
                Token token = response.body();
                cachedToken.setToken(token);
            } else {
                // TODO(javi.cabanas): 16/6/16 implement error callback
            }
        } catch (IOException e) {
            // TODO(javi.cabanas): 16/6/16 implement error callback
        }

    }

}
