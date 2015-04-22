package com.videonasocialmedia.videona.model.sources.rest;

import com.videonasocialmedia.videona.model.sources.rest.exception.TokenExpiredException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jca on 9/3/15.
 */
public class RestErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            if (r.getBody().toString().contains("expired")) {
                return new TokenExpiredException(cause);
            }
        }
        return cause;
    }
}
