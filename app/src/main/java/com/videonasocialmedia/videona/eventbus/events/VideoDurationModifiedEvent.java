package com.videonasocialmedia.videona.eventbus.events;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by jca on 29/7/15.
 */
public class VideoDurationModifiedEvent {
    public final Video video;

    public VideoDurationModifiedEvent(Video video) {
        this.video = video;
    }
}
