package com.videonasocialmedia.videona.eventbus.events;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by jca on 16/9/15.
 */
public class AddMediaItemToTrackSuccessEvent {
    public final Video videoAdded;
    public AddMediaItemToTrackSuccessEvent(Video video) {
        videoAdded=video;
    }
}
