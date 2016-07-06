/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.domain.usecase;

import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnLoginListener;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthClient;
import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.videona.repository.rest.ServiceGenerator;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
public class LoginUser {


    public boolean userIsLoggedIn() {
        return CachedToken.hasToken();
    }

    public void login(String email, String password, final OnLoginListener loginListener) {
        AuthClient authClient = new ServiceGenerator().generateService(AuthClient.class);
        AuthTokenRequest requestBody = new AuthTokenRequest(email, password);
        authClient.getAuthToken(requestBody).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                if (token != null) {
                    CachedToken.setToken(token);
                    loginListener.onLoginSuccess();
                } else
                    loginListener.onLoginError(OnLoginListener.Causes.UNKNOWN_ERROR);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loginListener.onLoginError(OnLoginListener.Causes.UNKNOWN_ERROR);
            }
        });
    }

}
