/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter implements OnVideosRetrieved {

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

    private SplitView splitView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public SplitPreviewPresenter(SplitView splitView, UserEventTracker userEventTracker) {
        this.splitView = splitView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void loadProjectVideo(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        splitView.showPreview(videoList);
        Video video = videoList.get(0);
        splitView.initSplitView(video.getFileStopTime() - video.getFileStartTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        splitView.showError("No videos");
    }


    public void splitVideo(Video video, int positionInAdapter, int timeMs) {
        Video copyVideo = new Video(video);
        copyVideo.setFileStartTime(timeMs);
        copyVideo.setFileStopTime(video.getFileStopTime());
        video.setFileStopTime(timeMs);
        video.setIsSplit(true);
        copyVideo.setIsSplit(true);
        addVideoToProjectUseCase.addVideoToProjectAtPosition(copyVideo, positionInAdapter + 1);
        userEventTracker.trackClipSplitted(currentProject);
    }

}



