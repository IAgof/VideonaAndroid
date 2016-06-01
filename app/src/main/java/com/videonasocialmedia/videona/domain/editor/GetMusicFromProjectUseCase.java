package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GetMusicFromProjectCallback;

/**
 * Created by jliarte on 31/05/16.
 */
public class GetMusicFromProjectUseCase {
    public Project project;

    public GetMusicFromProjectUseCase() {
        this.project = Project.getInstance(null, null, null);
    }

    public void getMusicFromProject(GetMusicFromProjectCallback listener) {
        Music music = null;
        try {
            music = (Music) project.getAudioTracks().get(0).getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onMusicRetrieved(music);
    }
}
