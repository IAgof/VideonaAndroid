package com.videonasocialmedia.videona.model.sources.rest.exception;

import retrofit.RetrofitError;

/**
 * Created by jca on 9/3/15.
 */
public class TokenExpiredException extends Exception {
    RetrofitError cause;
    public TokenExpiredException(RetrofitError cause){
        super();
        this.cause=cause;
    }
}
