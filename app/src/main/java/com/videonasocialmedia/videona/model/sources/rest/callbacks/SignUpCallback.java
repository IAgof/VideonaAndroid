package com.videonasocialmedia.videona.model.sources.rest.callbacks;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jca on 5/3/15.
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
