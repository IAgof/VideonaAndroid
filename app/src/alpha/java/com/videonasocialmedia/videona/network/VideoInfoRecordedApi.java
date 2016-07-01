/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network;



import com.google.gson.JsonObject;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by alvaro on 29/06/16.
 */
public interface VideoInfoRecordedApi {

    @POST("user/newvideo")
    Call<Video> sendVideoInfoRecorded(Video videoInfoRecorded);

    //@POST("/user/newvideorecorded")
    //void sendVideoInfoRecorded(@Body Video videoInfoRecorded, Callback response);
}
