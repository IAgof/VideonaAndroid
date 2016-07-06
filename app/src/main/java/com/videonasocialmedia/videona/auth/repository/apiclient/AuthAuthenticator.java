/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.apiclient;

import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 *
 */
public class AuthAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        AuthClient authClient = new ServiceGenerator().generateService(AuthClient.class);
        // Token newToken= authClient.refreshToken();
        Request.Builder builder = response.request().newBuilder();
        if (!CachedToken.hasToken()) {
            // TODO(javi.cabanas): 15/6/16 refresh token
            //builder.addHeader("Authorization", "fakeToken");
        } else {
            builder.addHeader("Authorization", CachedToken.getToken().getToken());
        }
        return builder.build();
    }
}
