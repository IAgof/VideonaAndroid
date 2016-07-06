package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.network.domain.usecase.SendInfoVideo;
import com.videonasocialmedia.videona.network.presenters.callback.OnSendInfoVideoListener;
import com.videonasocialmedia.videona.network.repository.model.VideoMetadataRequest;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideonaPlayerView;

import java.util.List;

/**
 *
 */
public class MusicListPresenter implements OnVideosRetrieved, OnSendInfoVideoListener {

    private List<Music> availableMusic;
    private MusicListView musicListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private SendInfoVideo sendInfoVideoUseCase;
    private VideonaPlayerView playerView;

    public MusicListPresenter(MusicListView musicListView, VideonaPlayerView playerView) {
        this.playerView = playerView;
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        availableMusic = getMusicListUseCase.getAppMusic();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        sendInfoVideoUseCase = new SendInfoVideo();
        this.musicListView = musicListView;
    }

    public void onCreate() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void onStart() {
        musicListView.showVideoList(availableMusic);
    }

    public void getAvailableMusic() {
        musicListView.showVideoList(availableMusic);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        playerView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
    }

    public void sendInfoVideoEdited(String videoToSharePath) {
        sendInfoVideoUseCase.sendMetadataVideo(videoToSharePath, VideoMetadataRequest.VIDEO_TYPE.Edited, this);
    }

    @Override
    public void onSendInfoVideoError(OnSendInfoVideoListener.Causes causes) {

    }

    @Override
    public void onSendInfoSuccess() {

    }
}
