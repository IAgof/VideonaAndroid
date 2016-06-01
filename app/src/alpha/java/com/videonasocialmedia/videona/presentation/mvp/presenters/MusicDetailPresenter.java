package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;

import java.util.List;


/**
 *
 */
public class MusicDetailPresenter {

    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private Project project;
    private MusicDetailView musicDetailView;
    private Music music;

    public MusicDetailPresenter(MusicDetailView musicDetailView) {
        this.musicDetailView = musicDetailView;
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        project = Project.getInstance(null, null, null);
    }

    public void onCreate(int musicId) {

        music = checkMusicOnProject(musicId);
    }

    private Music checkMusicOnProject(int musicId) {
        Music resultMusic = new Music(0, "", 0, 0, "");
        try {
            if (project.getAudioTracks().size() != 0
                    && project.getAudioTracks().get(0).getDuration() > 0) {
                resultMusic = (Music) project.getAudioTracks().get(0).getItems().getFirst();
                musicDetailView.showTitle(music.getTitle());
                musicDetailView.showAuthor(music.getTitle());
                musicDetailView.showImage(music.getIconResourceId());
                musicDetailView.setupScene(true);
            } else {
                GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
                List<Music> musicList = getMusicListUseCase.getAppMusic();

                for (Music music : musicList) {
                    if (musicId == music.getMusicResourceId()) {
                        musicDetailView.showTitle(music.getNameResourceId());
                        musicDetailView.showAuthor(music.getNameResourceId());
                        musicDetailView.showImage(music.getIconResourceId());
                        musicDetailView.showBackground(music.getColorResourceId());
                        musicDetailView.setupScene(false);
                        resultMusic = music;
                    }
                }
            }
        } catch (IndexOutOfBoundsException iob) {
        }
        return resultMusic;
    }

    public void removeMusic() {
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
    }

    public void removeAllMusic() {
        removeMusicFromProjectUseCase.removeAllMusic(0);
    }


    public void addMusic() {
        addMusicToProjectUseCase.addMusicToTrack(music, 0);
    }
}
