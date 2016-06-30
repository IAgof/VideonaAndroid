/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.domain.editor;

import android.location.Location;
import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.videona.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.videona.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideoAddedToTrackEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.network.VideoInfoRecordedApi;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.videona.presentation.views.location.DeviceLocation;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is used to add a new videos to the project.
 */
public class AddVideoToProjectUseCase {

    /**
     * Constructor.
     */
    public AddVideoToProjectUseCase() {
    }

    /**
     * @param videoPath
     */
    public void addVideoToTrack(String videoPath) {
        Video videoToAdd = new Video(videoPath);
        addInfoToVideo(videoToAdd);
        addVideoToTrack(videoToAdd);
      //  sendInfoVideoToBackend(videoToAdd);
    }



    private void addInfoToVideo(final Video videoToAdd) {

        String mediaPath = videoToAdd.getMediaPath();

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaPath);
        videoToAdd.setFileDuration(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        videoToAdd.setHeight(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
        videoToAdd.setWidth(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
        videoToAdd.setRotation(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)));
        videoToAdd.setBitRate(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)));
        videoToAdd.setDate(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

        videoToAdd.setSize(new File(mediaPath).length());
        videoToAdd.setTitle(new File(mediaPath).getName());

        DeviceLocation.getLastKnownLocation(VideonaApplication.getAppContext(), false, new DeviceLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                videoToAdd.setLocationLatitude(location.getLatitude());
                videoToAdd.setLocationLongitude(location.getLongitude());

            }
        });

    }

    public void addVideoToTrack(Video video) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
            EventBus.getDefault().post(new AddMediaItemToTrackSuccessEvent(video));
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            //TODO manejar error
        }
    }

    /**
     * @param videoPath
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(String videoPath, OnAddMediaFinishedListener listener) {
        Video videoToAdd = new Video(videoPath);
        addInfoToVideo(videoToAdd);
        addVideoToTrack(videoToAdd, listener);
        sendInfoVideoToBackend(videoToAdd);
    }

    private void sendInfoVideoToBackend(Video videoToAdd) {

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.fake.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.fake.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();



        VideoInfoRecordedApi service = retrofit.create(VideoInfoRecordedApi.class);
        service.sendVideoInfoRecorded(videoToAdd, new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    /**
     * @param video
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
            listener.onAddMediaItemToTrackSuccess(video);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoToProjectAtPosition(Video video, int position) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItemAt(position, video);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {

        }
    }

    public void addVideoListToTrack(List<Video> videoList, OnAddMediaFinishedListener listener) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            for (Video video : videoList) {
                mediaTrack.insertItem(video);
            }
            listener.onAddMediaItemToTrackSuccess(null);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }


}
