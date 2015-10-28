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

import com.videonasocialmedia.videona.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.videona.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.videona.eventbus.events.videosretrieved.VideosRemovedFromProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.videona.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideosUseCase {

    /**
     * Constructor.
     */
    public RemoveVideosUseCase() {
    }

    public void removeMediaItemsFromProject(OnRemoveMediaFinishedListener listener) {
        boolean correct = false;
        MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
        List<Media> list = new ArrayList<Media>(mediaTrack.getItems());
        for (Media media : list) {
            correct = removeVideoItemFromTrack(media, mediaTrack);
            if (!correct) break;
            Utils.removeVideo(media.getMediaPath());
        }
        if (correct) {
            listener.onRemoveMediaItemFromTrackSuccess();
        } else {
            listener.onRemoveMediaItemFromTrackError();
        }
        if(!mediaTrack.getItems().isEmpty()) {
            mediaTrack = new MediaTrack();
        }

        EventBus.getDefault().post(new VideosRemovedFromProjectEvent());
        EventBus.getDefault().post(new UpdateProjectDurationEvent(0));
        EventBus.getDefault().post(new NumVideosChangedEvent(0));
    }

    /**
     * Gets the path of the new video and remove it in the media track.
     *
     * @param video the video which wants to delete
     * @return bool if the item has been deleted from the track, return true. If it fails, return false
     */
    private boolean removeVideoItemFromTrack(Media video, MediaTrack mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
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
