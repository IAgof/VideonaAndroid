/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.apiclient;

import com.videonasocialmedia.videona.auth.domain.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *
 */
public interface AuthClient {

    @POST("login")
    @Headers("Content-Type: application/json")
    Call<Token> getAuthToken(@Body AuthTokenRequest requestBody);
}
