/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network.repository.model;

/**
 * Created by alvaro on 4/07/16.
 */
public class VideoResponse {

    private String backendResponse;

    public VideoResponse(){
        this.backendResponse = "ok";
    }

    public String getBackendResponse(){
        return backendResponse;
    }
}
