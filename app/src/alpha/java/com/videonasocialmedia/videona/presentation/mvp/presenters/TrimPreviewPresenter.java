/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter implements OnVideosRetrieved{

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;

    private TrimView trimView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public TrimPreviewPresenter(TrimView trimView, UserEventTracker userEventTracker) {
        this.trimView = trimView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
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

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        trimView.showPreview(videoList);
        Video video = videoList.get(0);
        //showTimeTags(video);
        trimView.showTrimBar(video.getFileStartTime(), video.getFileStopTime(), (int)video.getFileDuration());
    }

    private void showTimeTags(Video video) {
        trimView.refreshDurationTag(video.getDuration());
        trimView.refreshStartTimeTag(video.getFileStartTime());
        trimView.refreshStopTimeTag(video.getFileStopTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        trimView.showError("No videos");
    }

    private void modifyVideoStartTime(int startTime) {
        modifyVideoDurationUseCase.modifyVideoStartTime(videoToEdit, startTime);
    }

    private void modifyVideoFinishTime(int finishTime) {
        modifyVideoDurationUseCase.modifyVideoFinishTime(videoToEdit, finishTime);
    }

    public void setTrim(int startTimeMs, int finishTimeMs) {
        modifyVideoStartTime(startTimeMs);
        modifyVideoFinishTime(finishTimeMs);
        userEventTracker.trackClipTrimmed(currentProject);
    }
}



