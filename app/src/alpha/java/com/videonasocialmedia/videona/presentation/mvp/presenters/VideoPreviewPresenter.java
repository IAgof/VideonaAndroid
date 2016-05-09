/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoPreviewView;

import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class VideoPreviewPresenter implements OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getGetMediaListFromProjectUseCase;

    /**
     * Preview View
     */
    private VideoPreviewView videoPreviewView;

     public VideoPreviewPresenter(VideoPreviewView videoPreviewView) {
        this.videoPreviewView = videoPreviewView;
        getGetMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
    }

    public void onResume(){

    }

    public void onPause(){

    }

    public void init() {
        getGetMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public List<Media> checkVideosOnProject() {
        return getGetMediaListFromProjectUseCase.getMediaListFromProject();
    }

    public void update() {
        getGetMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        videoPreviewView.showPreview(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        videoPreviewView.showError("No videos");
    }
}
