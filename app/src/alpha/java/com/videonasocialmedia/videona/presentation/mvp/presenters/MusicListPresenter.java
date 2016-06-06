package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicListView;

import java.util.List;

/**
 *
 */
public class MusicListPresenter {

    private List<Music> availableMusic;
    private MusicListView musicListView;

    public MusicListPresenter(MusicListView musicListView) {
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        availableMusic = getMusicListUseCase.getAppMusic();
        this.musicListView = musicListView;
    }

    public void onStart() {
        musicListView.showVideoList(availableMusic);
    }

    public void getAvailableMusic() {
        musicListView.showVideoList(availableMusic);
    }
}
