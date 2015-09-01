package com.videonasocialmedia.videona.eventbus.events.preview;

/**
 * Created by Veronica Lago Fominaya on 01/09/2015.
 */
public class UpdateSeekBarDurationEvent {
    public final int projectDuration;

    /**
     * Update the seek bar duration
     * @param projectDuration the duration of the project
     */
    public UpdateSeekBarDurationEvent(int projectDuration){
        this.projectDuration=projectDuration;
    }
}
