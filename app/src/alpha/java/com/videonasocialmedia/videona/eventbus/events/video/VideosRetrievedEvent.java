package com.videonasocialmedia.videona.eventbus.events.video;

import android.support.annotation.Nullable;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 28/7/15.
 */
public abstract class VideosRetrievedEvent {
    public final List<Video> videoList;

    /**
     * Creates a VideosRetrievedEvent
     * @param videoList the retrieved video list. Null if not retrieved or empty
     */
    protected VideosRetrievedEvent(@Nullable List<Video> videoList){
        this.videoList=videoList;
    }
}
