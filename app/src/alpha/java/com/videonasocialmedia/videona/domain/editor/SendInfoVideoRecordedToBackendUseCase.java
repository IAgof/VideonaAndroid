/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.network.VideoInfoRecordedApi;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alvaro on 28/06/16.
 */
public class SendInfoVideoRecordedToBackendUseCase {


    public void sendInfoVideoRecorded(Video video){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VideoInfoRecordedApi service = retrofit.create(VideoInfoRecordedApi.class);

        service.sendVideoInfoRecorded(video, new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    // video data saved correctly
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // try again later?
            }
        });

    }

  /*  private void sendInfoToBackend(Video video) {
        JSONObject infoVideoRecorded = new JSONObject();
        try {
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_BITRATE, video.getBitRate());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_DATE, video.getDate());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_DURATION, video.getFileDuration());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_HEIGHT, video.getHeight());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_WIDTH,video.getWidth());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_ROTATION, video.getRotation());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_SIZE, video.getSize());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_LATITUDE, video.getLocationLatitude());
            infoVideoRecorded.put(InfoVideoConstants.VIDEO_LONGITUDE, video.getLocationLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
