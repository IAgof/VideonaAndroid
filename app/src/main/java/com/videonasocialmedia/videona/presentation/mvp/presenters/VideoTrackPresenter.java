/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddVideoUseCase;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoTrackView;

import java.util.ArrayList;

/**
 * Created by vlf on 27/04/2015.
 */
public class VideoTrackPresenter implements OnMediaFinishedListener {
    AddVideoUseCase addVideoUseCase;
    VideoTrackView videoTrackView;

    public VideoTrackPresenter(VideoTrackView videoTrackView) {
        addVideoUseCase = new AddVideoUseCase();
        this.videoTrackView = videoTrackView;
    }

    public void loadSelectedVideos(ArrayList<String> videos) {
        addVideoUseCase.loadSelectedMediaItems(videos, this);
    }

    @Override
    public void onAddMediaItemToTrackError() {
        videoTrackView.showError(R.string.addMediaItemToTrackError);
        throw new RuntimeException();
    }

    @Override
    public void onAddMediaItemToTrackSuccess(MediaTrack mediaTrack) {
        videoTrackView.drawMediaList(mediaTrack);
    }
}
