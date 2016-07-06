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
import android.util.Log;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.videona.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideoAddedToTrackEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.videona.presentation.views.location.DeviceLocation;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This class is used to add a new videos to the project.
 */
public class AddVideoToProjectUseCase {

    private final String TAG = this.getClass().getName();

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

    public void addVideoToTrack(Video video) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
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

    }

    /**
     * @param video
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener) {
        addInfoToVideo(video);
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
