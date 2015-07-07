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

import com.videonasocialmedia.videona.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
        OnRemoveMediaFinishedListener, OnExportFinishedListener {

    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    AddVideoToProjectUseCase addVideoToProjectUseCase;
    GalleryPagerView galleryPagerView;
    ExportProjectUseCase exportProjectUseCase;
    private boolean exported = false;

    /**
     * Constructor.
     */
    public GalleryPagerPresenter(GalleryPagerView galleryPagerView) {
        this.galleryPagerView = galleryPagerView;
        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
    }

    /**
     * This method is used to add new videos to the actual track.
     *
     * @param video the path of the new video which user wants to add to the project
     */
    public void loadVideoToProject(Video video) {
        //resetProject();
        addVideoToProjectUseCase.addVideoToTrack(video, this);
    }

    public void loadVideoListToProject(List<Video> videoList) {
        //resetProject();
        //exported= false;
        addVideoToProjectUseCase.addVideoListToTrack(videoList, this);
    }

    private void resetProject() {
        Project project = Project.getInstance(null, null, null);
        MediaTrack mediaTrack = project.getMediaTrack();
        LinkedList<Media> listMedia = mediaTrack.getItems();
        ArrayList<Media> items = new ArrayList<>(listMedia);
        if (items.size() > 0) {
            removeVideoFromProjectUseCase.removeMediaItemsFromProject(items, this);
        }

        removeMusicFromProjectUseCase.removeAllMusic(0, this);
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
    }

    @Override
    public void onAddMediaItemToTrackError() {

    }

    /*

    ÑAPA

     */

    @Override
    public void onAddMediaItemToTrackSuccess(Media video) {
        galleryPagerView.navigate();
        /*
        if (exported)
            galleryPagerView.navigate();
        else {
            exportProjectUseCase = new ExportProjectUseCase(this);
            exportProjectUseCase.export();
        }
        */
    }

    @Override
    public void onExportError(String error) {

    }

    @Override
    public void onExportSuccess(Video video) {
        //exported=true;
        loadVideoToProject(video);
    }
}
