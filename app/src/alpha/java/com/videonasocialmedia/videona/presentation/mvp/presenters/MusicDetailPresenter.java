package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;


/**
 *
 */
public class MusicDetailPresenter {

    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private Project project;


    public MusicDetailPresenter() {
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        project = Project.getInstance(null, null, null);
    }

    public void onCreate(int musicId) {

        if (isMusicInProject(musicId)) {
            //show project music in detail music view
        }
    }

    private boolean isMusicInProject(int musicId) {
        boolean isMusicInProject = false;
        try {
            if (project.getAudioTracks().size() != 0
                    && project.getAudioTracks().get(0).getDuration() > 0) {
                Music music = (Music) project.getAudioTracks().get(0).getItems().getFirst();
                if (music.getMusicResourceId() == musicId)
                    isMusicInProject = true;
            }
        } catch (IndexOutOfBoundsException iob) {
            return false;
        }
        return isMusicInProject;
    }

    public void setMusicIntoProject() {

    }

    public void addMusic(Music music) {
        addMusicToProjectUseCase.addMusicToTrack(music, 0);
    }

    public void removeMusic(Music music) {
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
    }

    public void removeAllMusic() {
        removeMusicFromProjectUseCase.removeAllMusic(0);
    }

}
