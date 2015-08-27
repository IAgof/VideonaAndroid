package com.videonasocialmedia.videona.eventbus.events.video;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by Veronica Lago Fominaya on 26/08/2015.
 */
public class VideoInsertedEvent {
    public final Video video;
    public final int position;

    public VideoInsertedEvent(Video video, int position) {
        this.video = video;
        this.position = position;
    }
}
