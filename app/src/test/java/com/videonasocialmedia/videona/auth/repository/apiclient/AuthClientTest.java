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
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

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
    private static final String CORRECT_REGISTER_RESPONSE
            = "{\"result\":\"User created\"}";
    private static final String ERROR_REGISTER_RESPONSE_MISSING_PARAMETERS
            = "{\"error\":  \"Missing request parameters\"}";
    private static final String ERROR_REGISTER_RESPONSE_EMAIL_NOT_VALID
            = "{\"error\":  \"Missing request parameters\"}";
    private static final String ERROR_REGISTER_RESPONSE_USER_ALREADY_EXISTS
            = "{\"error\":  \"Missing request parameters\"}";

    MockWebServer server;
    AuthClient authClient;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/");
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
        assertEquals("/auth", loginRequest.getPath());
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

    @Test
    public void ShouldReceiveRegisterUserCorrectly() throws IOException, InterruptedException {
        enqueCorrectRegisterResponse();
        Response<Map<String, String>> registerResponse = performRegister();
        RecordedRequest registerRequest = server.takeRequest();
        assertEquals("/users/register", registerRequest.getPath());
        assertEquals(true, registerResponse.body().containsKey("result"));
    }

    private void enqueCorrectRegisterResponse() {
        MockResponse correctRegisterResponse = new MockResponse()
                .setBody(CORRECT_REGISTER_RESPONSE);
        server.enqueue(correctRegisterResponse);
    }

    private Response<Map<String, String>> performRegister() throws IOException {
        return authClient.register(new RegisterRequest("foo", "bar")).execute();
    }

    @Test
    public void ShouldReceiveErrorRegisterUserCorrectly() throws IOException, InterruptedException {
        enqueErrorRegisterResponse(ERROR_REGISTER_RESPONSE_EMAIL_NOT_VALID);
        Response<Map<String, String>> registerResponse = performRegister();
        RecordedRequest registerRequest = server.takeRequest();
        assertEquals("/users/register", registerRequest.getPath());
        assertEquals(true, registerResponse.body().containsKey("error"));
    }

    private void enqueErrorRegisterResponse(String errorResponseMessage) {
        MockResponse correctRegisterResponse = new MockResponse()
                .setBody(errorResponseMessage);
        server.enqueue(correctRegisterResponse);
    }


}