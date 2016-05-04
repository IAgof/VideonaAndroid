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
import com.videonasocialmedia.videona.presentation.mvp.views.PreviewView;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter implements OnVideosRetrieved{

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

    private SplitView splitView;

    public SplitPreviewPresenter(PreviewView previewView, SplitView splitView) {
        this.previewView = previewView;
        this.splitView = splitView;
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
        Video video = videoList.get(0);
        splitView.initSplitView(video.getFileStopTime() - video.getFileStartTime());
        previewView.updateSeekBarSize();
    }

    @Override
    public void onNoVideosRetrieved() {
        previewView.showError("No videos");
    }


    public void splitVideo(Video video, int positionInAdapter, int timeMs) {
        Video copyVideo = new Video(video);
        copyVideo.setFileStartTime(timeMs);
        copyVideo.setFileStopTime(video.getFileStopTime());
        video.setFileStopTime(timeMs);
        addVideoToProjectUseCase.addVideoToProjectAtPosition(copyVideo, positionInAdapter + 1);
    }

}



