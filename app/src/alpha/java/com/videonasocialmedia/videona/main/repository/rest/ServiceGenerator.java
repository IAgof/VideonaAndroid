/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.main.repository.rest;

import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthAuthenticator;
import com.videonasocialmedia.videona.auth.repository.apiclient.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class ServiceGenerator {

    private OkHttpClient.Builder httpClientBuilder;
    private Retrofit.Builder retrofitBuilder;

    /**
     * Creates a ServiceGenerator with a default url
     */
    public ServiceGenerator() {
        this(null);
    }

    /**
     * Creates a ServiceGenerator
     *
     * @param ApiBaseUrl the url of the API
     */
    public ServiceGenerator(String ApiBaseUrl) {
        httpClientBuilder = new OkHttpClient.Builder();
        if (ApiBaseUrl != null)
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(ApiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create());
        else
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
    }

    public <T> T generateService(Class<T> serviceClass) {
        return generateService(serviceClass, null);
    }

    public <T> T generateService(Class<T> serviceClass, final Token token) {
        if (token != null) {
            AuthInterceptor authInterceptor = new AuthInterceptor(token);
            AuthAuthenticator authenticator = new AuthAuthenticator();
            httpClientBuilder.addInterceptor(authInterceptor)
                    .authenticator(authenticator);
        }

        OkHttpClient okClient = httpClientBuilder.build();

        Retrofit retrofit = retrofitBuilder.client(okClient).build();
        return retrofit.create(serviceClass);
    }

}
