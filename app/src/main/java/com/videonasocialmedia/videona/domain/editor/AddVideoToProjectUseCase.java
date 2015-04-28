/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.model.entities.social.Session;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnMediaFinishedListener;

import java.util.ArrayList;

/**
 * This class is used to add a new videos to the project.
 *
 * @author vlf
 * @since 27/04/2015
 */
public class AddVideoToProjectUseCase implements AddMediaToProjectUseCase {

    User user;
    ArrayList<User> authors;
    MediaTrack mediaTrack;

    /**
     * Constructor.
     */
    public AddVideoToProjectUseCase() {
        this.user = Session.getInstance().getUser();
        this.mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
    }

    @Override
    public void addMediaItemsToProject(ArrayList<String> list, OnMediaFinishedListener listener) {
        boolean correct = false;

        for (String path : list) {
            authors = new ArrayList<>();
            authors.add(user);
            // TODO: Add previous authors to the selected video
            correct = addMediaItemToTrack(path);
            if (!correct) break;
        }

        if (correct) {
            listener.onAddMediaItemToTrackSuccess(mediaTrack);
        } else {
            listener.onAddMediaItemToTrackError();
        }
    }

    /**
     * Gets the path of the new video and insert it in the media track.
     *
     * @param videoPath the path of the new video item
     * @return bool if the new item has been added to the track, return true. If it fails, return false
     */
    private boolean addMediaItemToTrack(String videoPath) {
        boolean result;
        try {
            mediaTrack.insertItem(new Video(null, null, videoPath, 0, 0, authors, null));
            result = true;
            // TODO: check if this file is a video object and if not throws a new exception
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        }
        return result;
    }
}
