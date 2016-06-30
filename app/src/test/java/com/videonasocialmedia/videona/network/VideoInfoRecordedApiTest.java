/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;

/**
 * Created by alvaro on 29/06/16.
 */
public class VideoInfoRecordedApiTest {

    MockWebServer server;
    VideoInfoRecordedApi videoInfoRecordedApi;

    @Before
    public void setUp() throws IOException {

        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/test/");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        videoInfoRecordedApi = retrofit.create(VideoInfoRecordedApi.class);

    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }
}
