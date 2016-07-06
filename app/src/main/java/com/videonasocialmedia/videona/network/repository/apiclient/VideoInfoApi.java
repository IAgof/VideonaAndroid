/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network.repository.apiclient;



import com.videonasocialmedia.videona.network.repository.model.VideoMetadataRequest;
import com.videonasocialmedia.videona.network.repository.model.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by alvaro on 29/06/16.
 */
public interface VideoInfoApi {

    @POST("videos/")
    @Headers("Content-Type: application/json")
    Call<VideoResponse> sendInfoVideo(@Body VideoMetadataRequest videoInfo);

}
