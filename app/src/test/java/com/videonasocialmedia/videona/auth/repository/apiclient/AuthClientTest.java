/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.repository.apiclient;

import com.videonasocialmedia.videona.auth.domain.model.Token;
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class AuthClientTest {

    private static final String ACCES_TOKEN_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    MockWebServer server;
    AuthClient authClient;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/test/");
        authClient = new ServiceGenerator(baseUrl.toString()).generateService(AuthClient.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }


    @Test
    public void ShouldReceiveCorrectlyFormattedToken() throws Exception {
        enqeueAuthResponse();
        Token receivedToken = performLogin();
        RecordedRequest loginRequest = server.takeRequest();
        assertEquals("/test/login", loginRequest.getPath());
        assertEquals(receivedToken.getToken(), ACCES_TOKEN_KEY);
    }

    private void enqeueAuthResponse() {
        MockResponse authResponse =
                new MockResponse().setBody("{\n" +
                        " \"access_token\":\"" + ACCES_TOKEN_KEY + "\"\n" +
                        "}");
        server.enqueue(authResponse);
    }

    private Token performLogin() throws IOException {
        Response<Token> response =
                authClient.getAuthToken(new AuthTokenRequest("foo", "bar")).execute();
        return response.body();
    }
}