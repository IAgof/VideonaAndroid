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
 * Created by vlf on 27/04/2015.
 */
public class AddVideoUseCase implements AddMediaUseCase {

    User user;
    ArrayList<User> authors;
    MediaTrack mediaTrack;

    public AddVideoUseCase() {
        this.user = Session.getInstance().getUser();
        this.mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
    }

    public void loadSelectedMediaItems(ArrayList<String> list, OnMediaFinishedListener listener) {
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
