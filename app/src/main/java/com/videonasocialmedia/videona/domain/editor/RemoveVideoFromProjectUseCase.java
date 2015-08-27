/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
*
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideoFromProjectUseCase implements RemoveMediaFromProjectUseCase {


    /**
     * Constructor.
     */
    public RemoveVideoFromProjectUseCase() {
    }

    @Override
    public void removeMediaItemsFromProject(ArrayList<Media> list, OnRemoveMediaFinishedListener listener) {
        boolean correct = false;
        MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
        for (Media media : list) {
            correct = removeVideoItemFromTrack(media, mediaTrack);
            if (!correct) break;
        }
        if (correct) {
            listener.onRemoveMediaItemFromTrackSuccess();
        } else {
            listener.onRemoveMediaItemFromTrackError();
        }
        EventBus.getDefault().post(new UpdateProjectDuration(Project.getInstance(null, null, null).getDuration()));
    }

    /**
     * Gets the path of the new video and insert it in the media track.
     *
     * @param video the video which wants to delete
     * @return bool if the item has been deleted from the track, return true. If it fails, return false
     */
    private boolean removeVideoItemFromTrack(Media video, MediaTrack mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            EventBus.getDefault().post(new UpdateProjectDuration(Project.getInstance(null, null, null).getDuration()));
            result = true;
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
            result = false;
        }
        return result;
    }
}
