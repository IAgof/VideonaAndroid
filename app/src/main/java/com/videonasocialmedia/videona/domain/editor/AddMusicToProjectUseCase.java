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
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;

/**
 * This class is used to add a new videos to the project.
 */
public class AddMusicToProjectUseCase {

    /**
     * Constructor.
     */
    public AddMusicToProjectUseCase() {


    }

    public void addMusicToTrack(Music music, int trackIndex, OnAddMediaFinishedListener listener) {
        AudioTrack audioTrack = obtainAudioTrack(trackIndex);
        try {
            audioTrack.insertItem(music);
            listener.onAddMediaItemToTrackSuccess(music);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            listener.onAddMediaItemToTrackError();
        }
    }

    private AudioTrack obtainAudioTrack(int trackIndex) {
        return Project.getInstance(null, null, null).getAudioTracks().get(trackIndex);
    }

}