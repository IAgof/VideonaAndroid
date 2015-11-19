package com.videonasocialmedia.videona.eventbus.events.videosretrieved;

import android.support.annotation.Nullable;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 28/7/15.
 */
public class VideosRetrieveFromDiskEvent extends VideosRetrievedEvent{
    public VideosRetrieveFromDiskEvent(@Nullable List<Video> videoList){
        super(videoList);
    }
}
