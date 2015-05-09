/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.sources.rest.callbacks;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Class that implements callback for sign up api request
 *
 * @author Juan Javier Cabanas Abascal
 */
public class SignUpCallback implements Callback<Response> {
    /**
     * Successful HTTP response.
     *
     * @param o
     * @param response
     */
    @Override
    public void success(Response o, Response response) {

        response.getStatus();
    }

    /**
     * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
     * exception.
     *
     * @param error
     */
    @Override
    public void failure(RetrofitError error) {

    }
}
