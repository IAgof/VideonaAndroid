/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;

import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class PreviewPresenter implements OnVideosRetrieved {

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
    private PreviewView previewView;

    public PreviewPresenter(PreviewView previewView) {
        this.previewView = previewView;
        getGetMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
    }

    public void init() {
        getGetMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void update() {
        getGetMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        previewView.showPreview(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        previewView.showError("No videos");
    }
}