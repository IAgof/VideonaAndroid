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

import com.videonasocialmedia.videona.eventbus.events.preview.UpdateSeekBarDurationEvent;
import com.videonasocialmedia.videona.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.videona.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideoInsertedErrorEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideoInsertedEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This class is used to add a new videos to the project.
 */
public class AddVideoToProjectUseCase {

    /**
     * Constructor.
     */
    public AddVideoToProjectUseCase() {
    }

    public void addVideoToTrack(String videoPath, OnAddMediaFinishedListener listener) {
        Video videoToAdd = new Video(videoPath);
        addVideoToTrack(videoToAdd, listener);
    }

    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
            listener.onAddMediaItemToTrackSuccess(video);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoToProjectAtPosition(Video video, int position) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItemAt(position, video);
            EventBus.getDefault().post(new VideoInsertedEvent(video, position));
            EventBus.getDefault().post(new UpdateSeekBarDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            EventBus.getDefault().post(new VideoInsertedErrorEvent(illegalItemOnTrack, "Fail to insert video"));
        }
    }

    public void addVideoListToTrack(List<Video> videoList, OnAddMediaFinishedListener listener){
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            for(Video video: videoList){
                mediaTrack.insertItem(video);
            }
            listener.onAddMediaItemToTrackSuccess(null);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }


}
