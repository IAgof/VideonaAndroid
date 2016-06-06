package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideonaPlayerView;

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


    public MusicDetailPresenter(MusicDetailView musicDetailView, VideonaPlayerView playerView) {
        this.musicDetailView = musicDetailView;
        this.playerView = playerView;
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
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
            setupScene(false);
        } else {
            this.music = music;
            setupScene(true);
        }
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
        musicDetailView.showTitle(music.getNameResourceId());
        musicDetailView.showAuthor(music.getNameResourceId());
        musicDetailView.showImage(music.getIconResourceId());
        musicDetailView.setupScene(isMusicOnProject);
    }

    public void removeMusic() {
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
    }


    public void addMusic() {
        addMusicToProjectUseCase.addMusicToTrack(music, 0);
        setupScene(true);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        playerView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO (javi.cabanas) show error
    }
}
