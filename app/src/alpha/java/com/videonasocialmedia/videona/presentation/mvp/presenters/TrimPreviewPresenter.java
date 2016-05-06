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
import com.videonasocialmedia.videona.presentation.mvp.views.VideoPreviewView;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;

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

    /**
     * Preview View
     */
    private VideoPreviewView videoPreviewView;

    private TrimView trimView;

    public TrimPreviewPresenter(VideoPreviewView videoPreviewView, TrimView trimView) {
        this.videoPreviewView = videoPreviewView;
        this.trimView = trimView;
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
        videoPreviewView.showPreview(videoList);
        Video video = videoList.get(0);
        if(video.getIsSplit()){
            showTimeSplittedTags(video);
            trimView.showTrimBar((video.getFileStopTime() - video.getFileStartTime()),0, (video.getFileStopTime() - video.getFileStartTime()) );
        } else {
            showTimeTags(video);
            trimView.showTrimBar(video.getFileDuration(), video.getFileStartTime(), video.getFileStopTime());
        }
    }

    private void showTimeTags(Video video) {
        trimView.refreshDurationTag(video.getDuration());
        trimView.refreshStartTimeTag(video.getFileStartTime());
        trimView.refreshStopTimeTag(video.getFileStopTime());
    }

    private void showTimeSplittedTags(Video video) {
        trimView.refreshDurationTag(video.getFileStopTime() - video.getFileStartTime());
        trimView.refreshStartTimeTag(0);
        trimView.refreshStopTimeTag(video.getFileStopTime() - video.getFileStartTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        videoPreviewView.showError("No videos");
    }


    public void modifyVideoStartTime(int startTime) {
        modifyVideoDurationUseCase.modifyVideoStartTime(videoToEdit, startTime);
    }

    public void modifyVideoFinishTime(int finishTime) {
        modifyVideoDurationUseCase.modifyVideoFinishTime(videoToEdit, finishTime);
    }

    public void onEvent (VideoDurationModifiedEvent event){
        Video modifiedVideo= event.video;
        videoPreviewView.updateSeekBarSize();
        showTimeTags(modifiedVideo);
    }

}



