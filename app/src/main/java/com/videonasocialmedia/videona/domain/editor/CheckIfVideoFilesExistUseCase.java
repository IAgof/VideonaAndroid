/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnVideosRetrieved;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckIfVideoFilesExistUseCase implements OnVideosRetrieved, OnRemoveMediaFinishedListener {

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;

    public void check() {
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        ArrayList<Media> mediasToDeleteFromProject = new ArrayList<>();
        for(Video video : videoList) {
            String path = video.getMediaPath();
            if(!fileExists(path)) {
                mediasToDeleteFromProject.add(video);
            }
        }
        if(mediasToDeleteFromProject.size() > 0) {
            removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
            removeVideoFromProjectUseCase.removeMediaItemsFromProject(mediasToDeleteFromProject, this);
        }
    }

    private boolean fileExists(String path) {
        boolean result;
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void onNoVideosRetrieved() {

    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {

    }
}
