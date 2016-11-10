/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.apiclient;

import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.videona.auth.repository.model.RegisterRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *
 */
public interface AuthClient {

    @POST("users/register")
    @Headers("Content-Type: application/json")
    Call<Map<String, String>> register(@Body RegisterRequest requestBody);

    @POST("auth")
    @Headers("Content-Type: application/json")
    Call<Token> getAuthToken(@Body AuthTokenRequest requestBody);

}
