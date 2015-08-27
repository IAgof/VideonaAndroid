package com.videonasocialmedia.videona.eventbus.events.project;

/**
 * Created by Veronica Lago Fominaya on 27/08/2015.
 */
public class UpdateProjectDuration {
    public final int projectDuration;

    /**
     * Update the project duration
     * @param projectDuration the duration of the project
     */
    public UpdateProjectDuration(int projectDuration){
        this.projectDuration=projectDuration;
    }
}
