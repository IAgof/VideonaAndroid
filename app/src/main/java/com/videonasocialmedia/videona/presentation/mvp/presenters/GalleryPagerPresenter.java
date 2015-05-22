/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoTrackView;

import java.util.ArrayList;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener {
    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    AddVideoToProjectUseCase addVideoToProjectUseCase;
    VideoTrackView videoTrackView;

    /**
     * Constructor.
     *
     * @param path the path of the video item
     */
    public GalleryPagerPresenter(String path) {
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        this.videoTrackView = videoTrackView;
    }

    /**
     * This method is used to add new videos to the actual track which makes use of addVideosToProject
     * method.
     *
     * @param videos the list of the paths of the new elements which user wants to add to the project
     */
    public void addVideosToProject(ArrayList<String> videos) {
        addVideoToProjectUseCase.addMediaItemsToProject(videos, this);
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
