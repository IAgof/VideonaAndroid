/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddAudioFinishedListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class is used to add a new audio to the project.
 *
 * @author vlf
 * @since 29/04/2015
 */
public class AddAudioToProjectUseCase {

    ArrayList<AudioTrack> audioTracks;

    /**
     * Constructor.
     */
    public AddAudioToProjectUseCase() {
        this.audioTracks = Project.getInstance(null, null, null).getAudioTracks();
    }

    /**
     * Gets the new audio and insert it in the audio track.
     *
     * @param audioTrackId the identifier of the audio track in which user wants to add the new audio
     *                     Now, we pass 0 to this method because there is only one audio track in the
     *                     project
     * @param audio        the audio item
     * @param listener     the listener which monitoring when this items have been added to the project
     */
    public void addAudioToProject(int audioTrackId, Audio audio, OnAddAudioFinishedListener listener) {
        boolean correct = false;

        AudioTrack audioTrack = audioTracks.get(audioTrackId);
        correct = addAudioItemToTrack(audio, audioTrack);

        if (correct) {
            listener.onAddAudioItemToTrackSuccess(audioTracks);
        } else {
            listener.onAddAudioItemToTrackError();
        }
    }

    /**
     * Gets the new audio and insert it in the audio track.
     *
     * @param audio      the audio item
     * @param audioTrack the audio track in which we want to add the new audio
     * @return bool if the new item has been added to the track, return true. If it fails, return false
     */
    private boolean addAudioItemToTrack(Audio audio, AudioTrack audioTrack) {
        boolean result;
        // TODO: Delete this conditional clause when we will have more than one audio items
        LinkedList<Media> previousAudios = audioTrack.getItems();
        if (previousAudios != null) {
            for (Media previousAudio : previousAudios) {
                RemoveAudioFromProjectUseCase removeAudioFromProjectUseCase = new RemoveAudioFromProjectUseCase();
                removeAudioFromProjectUseCase.removeAudioFromProject(0, audio, previousAudio);
            }
        }
        try {
            audioTrack.insertItem(audio);
            result = true;
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        }
        return result;
    }

}
