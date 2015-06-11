package com.videonasocialmedia.videona.model.sources;


import com.squareup.okhttp.OkHttpClient;
import com.videonasocialmedia.videona.model.entities.social.Session;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.model.entities.social.Video;
import com.videonasocialmedia.videona.model.sources.rest.ApiHeaders;
import com.videonasocialmedia.videona.model.sources.rest.RestErrorHandler;
import com.videonasocialmedia.videona.model.sources.rest.VideonaApi;
import com.videonasocialmedia.videona.model.sources.rest.callbacks.SignUpCallback;
import com.videonasocialmedia.videona.model.sources.rest.requests.RegisterRequestBody;
import com.videonasocialmedia.videona.utils.Constants;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

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
public class VideonaRestSource implements DataSource {

    private static VideonaRestSource INSTANCE;

    //CustomCookieManager manager;
    RestAdapter restAdapter;
    private VideonaApi apiClient;
    private OkHttpClient okClient;
    private ApiHeaders apiHeaders;

    private VideonaRestSource() {
        //rest client setup
        okClient = new OkHttpClient();
        apiHeaders = new ApiHeaders();
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.API_ENDPOINT)
                .setRequestInterceptor(apiHeaders)
                .setErrorHandler(new RestErrorHandler())
                .build();
        apiClient = restAdapter.create(VideonaApi.class);
    }

    /**
     * @return a single instance of VideonaRestResource
     */
    public static VideonaRestSource getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new VideonaRestSource();
        }
        return INSTANCE;
    }

    /**
     * Register a new User in Videona Services
     *
     * @param userName
     * @param email
     * @param password
     */
    @Override
    public void createUser(String userName, String email, String password) {
        RegisterRequestBody registerRequestBody = new RegisterRequestBody(userName,
                email, password);
        apiClient.register(registerRequestBody, new SignUpCallback());
    }

    @Override
    public Video getVideo() {
        return null;
    }

    @Override
    public User getUser(int id) {
        //TODO reimplementar el código acorde a las especificaciones del api
        apiClient.getUserProfile(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                setUserName(response.getBody().toString());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        User user = null;
        if (userName != null) {
            user = new User(userName);
        }
        return user;
    }

    @Override
    public User getUser(String name) {
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }


    //PROVISIONAL
    private void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;
}
