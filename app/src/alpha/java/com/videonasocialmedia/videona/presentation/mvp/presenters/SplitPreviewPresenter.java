/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videona.eventbus.events.VideoDurationModifiedEvent;
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

    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;

    /**
     * Preview View
     */
    private PreviewView previewView;

    private SplitView splitView;

    public SplitPreviewPresenter(PreviewView previewView, SplitView splitView) {
        this.previewView = previewView;
        this.splitView = splitView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
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
        showTimeTags(video);
        splitView.showSplitBar(video.getFileDuration(), video.getFileStartTime()/2);
    }

    private void showTimeTags(Video video) {
        splitView.refreshTimeTag(video.getDuration());
    }

    @Override
    public void onNoVideosRetrieved() {
        previewView.showError("No videos");
    }


    public void modifyVideoStartTime(int startTime) {
        modifyVideoDurationUseCase.modifyVideoStartTime(videoToEdit, startTime);
    }

    public void modifyVideoFinishTime(int finishTime) {
        modifyVideoDurationUseCase.modifyVideoFinishTime(videoToEdit, finishTime);
    }

    public void onEvent (VideoDurationModifiedEvent event){
        Video modifiedVideo= event.video;
        previewView.updateSeekBarSize();
        showTimeTags(modifiedVideo);
    }

}



