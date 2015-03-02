package com.videonasocialmedia.videona.model.rest.requests;


import com.squareup.okhttp.OkHttpClient;

import com.videonasocialmedia.videona.common.utils.Constants;
import com.videonasocialmedia.videona.model.rest.ApiHeaders;
import com.videonasocialmedia.videona.model.rest.VideonaApi;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */

/**
 * Class implementing a rest source of data
 */
public class VideonaRestSource {

    private static VideonaRestSource INSTANCE;

    //CustomCookieManager manager;
    RestAdapter restAdapter;
    private VideonaApi apiClient;
    private OkHttpClient okClient;
    private ApiHeaders apiHeaders;

    private VideonaRestSource(){
        //rest client setup
        okClient = new OkHttpClient();
        //manager = new CustomCookieManager();
        apiHeaders = new ApiHeaders();
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)

                .setEndpoint(Constants.API_ENDPOINT)
                .setRequestInterceptor(apiHeaders)
                .build();
        apiClient = restAdapter.create(VideonaApi.class);
    }

    /**
     *
     * @return a single instance of VideonaRestResource
     */
    public VideonaRestSource getInstance(){

        if (INSTANCE==null){
            INSTANCE= new VideonaRestSource();
        }
        return INSTANCE;
    }

    public g
}
