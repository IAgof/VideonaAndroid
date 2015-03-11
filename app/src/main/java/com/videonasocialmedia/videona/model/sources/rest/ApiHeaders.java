package com.videonasocialmedia.videona.model.sources.rest;

import com.videonasocialmedia.videona.model.entities.social.Session;

import retrofit.RequestInterceptor;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */
public class ApiHeaders implements RequestInterceptor {


    /**
     * Called for every request. Add data using methods on the supplied {@link retrofit.RequestInterceptor.RequestFacade}.
     *
     * @param request api call request message
     */
    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "Videona-App");
        Session session= Session.getInstance();
        if (Session.getInstance().hasToken()) {
            request.addHeader("Authorization", "Bearer " + session.getAuthToken().getToken());
        }
    }
}
