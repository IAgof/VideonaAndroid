/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class DuplicatePreviewPresenter implements OnVideosRetrieved{

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private AddVideoToProjectUseCase addVideoToProjectUseCase;

    /**
     * Preview View
     */
    private PreviewView previewView;

    private DuplicateView duplicateView;

    public DuplicatePreviewPresenter(PreviewView previewView, DuplicateView duplicateView) {
        this.previewView = previewView;
        this.duplicateView = duplicateView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
    }

    public void init(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }

    }

    public void onResume(){

    }

    public void onPause(){

    }


    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        previewView.showPreview(videoList);
        duplicateView.initDuplicateView(videoList.get(0).getMediaPath());

    }

    @Override
    public void onNoVideosRetrieved() {
        previewView.showError("No videos");
    }


    public void duplicateVideo(Video video, int positionInAdapter, int numDuplicates) {

    }

}



