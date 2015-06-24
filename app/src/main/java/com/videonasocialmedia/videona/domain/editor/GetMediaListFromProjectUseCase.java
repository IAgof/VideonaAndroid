/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.Track;

import java.util.LinkedList;

public class GetMediaListFromProjectUseCase {

    /**
     * Project of app
     */
    Project project;

    /**
     * Track
     */
    Track track;

    public GetMediaListFromProjectUseCase() {

        this.project = Project.getInstance(null, null, null);
        this.track = project.getMediaTrack();

    }

    public LinkedList<Media> getMediaListFromProject() {

        return track.getItems();
    }


}
