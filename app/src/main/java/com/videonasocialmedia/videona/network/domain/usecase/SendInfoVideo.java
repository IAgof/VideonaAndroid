/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network.domain.usecase;

import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.main.repository.rest.ServiceGenerator;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.network.presenters.callback.OnSendInfoVideoListener;
import com.videonasocialmedia.videona.network.repository.apiclient.VideoInfoApi;
import com.videonasocialmedia.videona.network.repository.model.VideoMetadataRequest;
import com.videonasocialmedia.videona.network.repository.model.VideoResponse;
import com.videonasocialmedia.videona.presentation.views.location.DeviceLocation;


import java.io.File;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alvaro on 28/06/16.
 */
public class SendInfoVideo {


    public void sendMetadataVideo(String mediaPath, VideoMetadataRequest.VIDEO_TYPE videoType, final OnSendInfoVideoListener listener){

        VideoInfoApi videoInfoApi = new ServiceGenerator().generateService(VideoInfoApi.class);
        Call<VideoResponse> call = videoInfoApi.sendInfoVideo(getVideoMetadata(mediaPath, videoType));
        call.enqueue(new Callback<VideoResponse>() {

            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                VideoResponse videoResponse = response.body();
                if(videoResponse != null){
                    //Process response
                    listener.onSendInfoSuccess();
                } else {
                    listener.onSendInfoVideoError(OnSendInfoVideoListener.Causes.UNKNOWN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                listener.onSendInfoVideoError(OnSendInfoVideoListener.Causes.UNKNOWN_ERROR);
            }
        });


    }



    private VideoMetadataRequest getVideoMetadata(String mediaPath, VideoMetadataRequest.VIDEO_TYPE videoType){

        Video video = getVideo(mediaPath, videoType);

        VideoMetadataRequest videoMetadata = new VideoMetadataRequest(video.getLocationLatitude(),
                video.getLocationLongitude(),video.getHeight(), video.getWidth(),
                video.getRotation(), video.getFileDuration(), video.getSize(),
                video.getDate(), video.getBitRate(),video.getTitle(), videoType);

        return videoMetadata;
    }

    private Video getVideo(String mediaPath, VideoMetadataRequest.VIDEO_TYPE videoType) {
        Video video;
        if(videoType.compareTo(VideoMetadataRequest.VIDEO_TYPE.Recorded) == 0) {
            video = getVideoFromProject(mediaPath);
        } else {
            video = new Video(mediaPath);
            addInfoToVideo(video);
        }
        return video;
    }


    private Video getVideoFromProject(String mediaPath) {

        Project project= Project.getInstance(null,null,null);
        LinkedList<Media> items = project.getMediaTrack().getItems();
        for(int numItem = 0; numItem < items.size(); numItem++){
            if(mediaPath.compareTo(items.get(numItem).getMediaPath()) == 0){
                return (Video) items.get(numItem);
            }
        }

        return new Video(mediaPath);
    }

    private void addInfoToVideo(final Video videoToAdd) {

        String mediaPath = videoToAdd.getMediaPath();

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaPath);
        videoToAdd.setFileDuration(Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        videoToAdd.setHeight(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
        videoToAdd.setWidth(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
        videoToAdd.setRotation(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)));
        videoToAdd.setBitRate(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)));
        videoToAdd.setDate(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

        File f = new File(mediaPath);
        videoToAdd.setSize(f.length());
        videoToAdd.setTitle(f.getName());

        DeviceLocation.getLastKnownLocation(VideonaApplication.getAppContext(), false, new DeviceLocation.LocationResult() {
            @Override
            public void gotLocationLatLng(double latitude, double longitude) {
                videoToAdd.setLocationLatitude(latitude);
                videoToAdd.setLocationLongitude(longitude);
            }
        });

    }
}
