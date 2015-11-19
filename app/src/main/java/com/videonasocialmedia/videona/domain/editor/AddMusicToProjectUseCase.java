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


import com.videonasocialmedia.videona.eventbus.events.music.ErrorAddingMusicToProjectEvent;
import com.videonasocialmedia.videona.eventbus.events.music.MusicAddedToProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;

import de.greenrobot.event.EventBus;

/**
 * This class is used to add a new videos to the project.
 */
public class AddMusicToProjectUseCase {


    /**
     * @deprecated use instead the the method withoutlistener and register your listener using event bus
     * @param music
     * @param trackIndex
     * @param listener
     *
     */
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

    public void addMusicToTrack(Music music, int trackIndex) {
        AudioTrack audioTrack = obtainAudioTrack(trackIndex);
        try {
            audioTrack.insertItem(music);
            EventBus.getDefault().post(new MusicAddedToProjectEvent(music));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            EventBus.getDefault().post(new ErrorAddingMusicToProjectEvent(music));
        }
    }

    private AudioTrack obtainAudioTrack(int trackIndex) {
        return Project.getInstance(null, null, null).getAudioTracks().get(trackIndex);
    }

}
