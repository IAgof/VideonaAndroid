/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveAudioFinishedListener;

import java.util.ArrayList;

/**
 * This class is used to remove an existed audio from the project.
 *
 * @author vlf
 * @since 29/04/2015
 */
public class RemoveAudioFromProjectUseCase {

    ArrayList<AudioTrack> audioTracks;

    /**
     * Constructor.
     */
    public RemoveAudioFromProjectUseCase() {
        this.audioTracks = Project.getInstance(null, null, null).getAudioTracks();
    }

    /**
     * Gets the audio and remove it from the audio track.
     *
     * @param audioTrackId the identifier of the audio track in which user wants to remove the audio
     *                     Now, we pass 0 to this method because there is only one audio track in the
     *                     project
     * @param audio        the audio item
     * @param listener     the listener which monitoring when this items have been added to the project
     */
    public void removeAudioFromProject(int audioTrackId, Audio audio, OnRemoveAudioFinishedListener listener) {
        boolean correct = false;

        AudioTrack audioTrack = audioTracks.get(audioTrackId);
        correct = removeAudioItemFromTrack(audio, audioTrack);

        if (correct) {
            listener.onRemoveAudioItemFromTrackSuccess(audioTracks);
        } else {
            listener.onRemoveAudioItemFromTrackError();
        }
    }

    /**
     * Gets the audio and remove it from the audio track.
     *
     * @param audio      the audio item
     * @param audioTrack the audio track in which we want to add the new audio
     * @return bool if the new item has been added to the track, return true. If it fails, return false
     */
    private boolean removeAudioItemFromTrack(Audio audio, AudioTrack audioTrack) {
        boolean result;
        try {
            audioTrack.deleteItem(audio);
            result = true;
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            result = false;
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        }
        return result;
    }
}
