/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.domain.usecase;

import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnRegisterListener;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthClient;
import com.videonasocialmedia.videona.auth.repository.model.RegisterRequest;
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
public class RegisterUser {
    public void register(String email, String password, final OnRegisterListener registerListener) {
        AuthClient authClient = new ServiceGenerator().generateService(AuthClient.class);
        RegisterRequest requestBody = new RegisterRequest(email, password);
        authClient.register(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Map<String, String> responseBody = response.body();
                OnRegisterListener.Causes cause = OnRegisterListener.Causes.UNKNOWN_ERROR;
                if (responseBody.containsKey("error")) {
                    switch (responseBody.get("error")) {
                        case "Missing request parameters":
                            cause = OnRegisterListener.Causes.MISSING_REQUEST_PARAMETERS;
                            break;
                        case "Password too short. Type at least 6 characters":
                            cause = OnRegisterListener.Causes.INVALID_PASSWORD;
                            break;
                        case "Email not valid":
                            cause = OnRegisterListener.Causes.INVALID_EMAIL;
                            break;
                        case "User already exists":
                            cause = OnRegisterListener.Causes.USER_ALREADY_EXISTS;
                            break;
                    }
                    registerListener.onRegisterError(cause);
                } else {
                    registerListener.onRegisterSuccess();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                registerListener.onRegisterError(OnRegisterListener.Causes.NETWORK_ERROR);
            }
        });
    }
}
