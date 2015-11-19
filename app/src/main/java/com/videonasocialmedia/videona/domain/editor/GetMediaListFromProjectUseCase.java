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
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnVideosRetrieved;

import java.util.List;

public class GetMediaListFromProjectUseCase {

    /**
     * @return
     */
    public List<Media> getMediaListFromProject() {
        Project project=Project.getInstance(null, null, null);
        Track track=project.getMediaTrack();
        return track.getItems();
    }

    @Deprecated
    public void getMediaListFromProject(OnVideosRetrieved listener){
        Project project=Project.getInstance(null, null, null);
        Track track=project.getMediaTrack();
        List items= track.getItems();
        if (items.size()>0)
            listener.onVideosRetrieved(items);
        else
            listener.onNoVideosRetrieved();
    }
}
