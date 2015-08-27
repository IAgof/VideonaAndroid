package com.videonasocialmedia.videona.eventbus.events.project;

/**
 * Created by Veronica Lago Fominaya on 27/08/2015.
 */
public class UpdateProjectDurationEvent {
    public final int projectDuration;

    /**
     * Update the project duration
     * @param projectDuration the duration of the project
     */
    public UpdateProjectDurationEvent(int projectDuration){
        this.projectDuration=projectDuration;
    }
}
