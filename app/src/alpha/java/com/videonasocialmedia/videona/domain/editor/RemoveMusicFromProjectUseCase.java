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


import android.util.Log;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.List;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveMusicFromProjectUseCase {

    /**
     * @param music      the music object to be removed
     * @param trackIndex the index of the track where de music is placed
     * @param listener   the listener waiting for the music being removed
     * @deprecated the method is deprecated because of the recent use of event buses
     */
    @Deprecated
    public void removeMusicFromProject(Music music, int trackIndex, OnRemoveMediaFinishedListener listener) {
        AudioTrack audioTrack = Project.getInstance(null, null, null)
                .getAudioTracks().get(trackIndex);
        for (int musicIndex = 0; musicIndex < audioTrack.getItems().size(); musicIndex++) {
            Media audio = audioTrack.getItems().get(musicIndex);
            if (audio.equals(music)) {
                try {
                    audioTrack.deleteItemAt(musicIndex);
                    listener.onRemoveMediaItemFromTrackSuccess();
                } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
                    //TODO treat exception properly
                }
            }
        }
    }

    /**
     * @param music      the music object to be removed
     * @param trackIndex the index of the track where de music is placed
     */
    public void removeMusicFromProject(Music music, int trackIndex) {
        AudioTrack audioTrack = Project.getInstance(null, null, null)
                .getAudioTracks().get(trackIndex);
        for (int musicIndex = 0; musicIndex < audioTrack.getItems().size(); musicIndex++) {
            Media audio = audioTrack.getItems().get(musicIndex);
            if (audio.equals(music)) {
                try {
                    audioTrack.deleteItemAt(musicIndex);
                } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
                    //TODO treat exception properly
                }
            }
        }
    }

    /**
     * @param trackIndex the index of the track where de music is removed
     * @param listener   the listener waiting for the music list being removed
     * @deprecated the method is deprecated because of the recent use of event buses
     */
    public void removeAllMusic(int trackIndex, OnRemoveMediaFinishedListener listener) {
        try {
            List<AudioTrack> audioTracks = Project.getInstance(null, null, null).getAudioTracks();
            audioTracks.remove(trackIndex);
            audioTracks.add(new AudioTrack());
            listener.onRemoveMediaItemFromTrackSuccess();
        } catch (Exception exception) {
            Log.e("REMOVE MUSIC", "removeAllMusic", exception);
        }
    }

    public void removeAllMusic(int trackIndex) {
        try {
            List<AudioTrack> audioTracks = Project.getInstance(null, null, null).getAudioTracks();
            audioTracks.remove(trackIndex);
            audioTracks.add(new AudioTrack());
        } catch (Exception exception) {
            Log.e("REMOVE MUSIC", "removeAllMusic", exception);
        }
    }
}
