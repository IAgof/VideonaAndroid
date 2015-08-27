package com.videonasocialmedia.videona.eventbus.events.video;

/**
 * Created by Veronica Lago Fominaya on 26/08/2015.
 */
public class NumVideosChangedEvent {
    public final int numVideos;

    public NumVideosChangedEvent(int numVideos) {
        this.numVideos = numVideos;
    }
}
