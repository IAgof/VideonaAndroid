/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.domain.usecase;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnLoginListener;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthClient;
import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.videona.repository.rest.ServiceGenerator;


import java.io.IOException;

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
                } else {
                    OnLoginListener.Causes error_code = OnLoginListener.Causes.UNKNOWN_ERROR;
                    if (response.code() == 401) {
                        try {
                            String errorString = response.errorBody().string();
                            if (errorString.contains("Invalid credentials")) {
                                error_code = OnLoginListener.Causes.CREDENTIALS_UNKNOWN;
                            }
                        } catch (IOException e) {}
                    }
                    loginListener.onLoginError(error_code);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loginListener.onLoginError(OnLoginListener.Causes.UNKNOWN_ERROR);
            }
        });
    }

}
