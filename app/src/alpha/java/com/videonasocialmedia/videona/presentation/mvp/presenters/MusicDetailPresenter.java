package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import java.util.List;


/**
 *
 */
public class MusicDetailPresenter implements GetMusicFromProjectCallback, OnVideosRetrieved {

    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    private MusicDetailView musicDetailView;
    private VideonaPlayerView playerView;
    private Music music;
    private int musicId;
    private boolean musicAddedToProject;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public MusicDetailPresenter(MusicDetailView musicDetailView, VideonaPlayerView playerView,
                                UserEventTracker userEventTracker) {
        this.musicDetailView = musicDetailView;
        this.playerView = playerView;
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void onCreate(int musicId) {
        this.musicId = musicId;
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        getMusicFromProjectUseCase.getMusicFromProject(this);
    }


    @Override
    public void onMusicRetrieved(Music music) {
        if (music == null) {
            this.music = retrieveLocalMusic(musicId);
            musicAddedToProject = false;
        } else {
            this.music = music;
            musicAddedToProject = true;
        }
        setupScene(musicAddedToProject);
        playerView.setMusic(this.music);
    }

    private Music retrieveLocalMusic(int musicId) {
        Music result = new Music(R.drawable.imagebutton_music_background_rock, "audio_rock", R.raw.audio_rock, R.color.rock, "author");
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        List<Music> musicList = getMusicListUseCase.getAppMusic();

        for (Music music : musicList) {
            if (musicId == music.getMusicResourceId()) {
                result = music;
            }
        }
        return result;
    }

    private void setupScene(boolean isMusicOnProject) {
        musicDetailView.showTitle(music.getMusicTitle());
        musicDetailView.showAuthor(music.getAuthor());
        musicDetailView.showImage(music.getIconResourceId());
        musicDetailView.setupScene(isMusicOnProject);
    }

    public void removeMusic() {
        musicAddedToProject = false;
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
        userEventTracker.trackMusicSet(currentProject);
    }


    public void addMusic() {
        musicAddedToProject = true;
        addMusicToProjectUseCase.addMusicToTrack(music, 0);
        userEventTracker.trackMusicSet(currentProject);
        setupScene(musicAddedToProject);
        musicDetailView.goToEdit(music.getMusicTitle());
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        playerView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO (javi.cabanas) show error
    }

    public boolean isMusicAddedToProject() {
        return musicAddedToProject;
    }
}
