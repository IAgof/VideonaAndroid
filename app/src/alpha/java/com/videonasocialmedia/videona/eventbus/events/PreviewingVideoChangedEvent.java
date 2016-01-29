package com.videonasocialmedia.videona.eventbus.events;

/**
 * Created by jca on 25/8/15.
 */
public class PreviewingVideoChangedEvent {
    public final int previewingVideoIndex;
    public final boolean fromUser;

    public PreviewingVideoChangedEvent(int previewingVideoIndex, boolean fromUser) {
        this.previewingVideoIndex = previewingVideoIndex;
        this.fromUser=fromUser;
    }
}
