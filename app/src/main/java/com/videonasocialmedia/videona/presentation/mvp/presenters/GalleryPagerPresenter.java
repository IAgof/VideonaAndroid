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
import com.videonasocialmedia.videona.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener, OnRemoveMediaFinishedListener {

    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    //GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    AddVideoToProjectUseCase addVideoToProjectUseCase;
    ArrayList<String> itemsToAdd;
    GalleryPagerView galleryPagerView;

    /**
     * Constructor.
     */
    public GalleryPagerPresenter(GalleryPagerView galleryPagerView) {
        this.galleryPagerView = galleryPagerView;
        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        //getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        itemsToAdd = new ArrayList<>();
    }

    /**
     * This method is used to add new videos to the actual track.
     *
     * @param videoPath the path of the new video which user wants to add to the project
     */
    public void loadVideoToProject(String videoPath) {
        itemsToAdd.add(videoPath);

        // LinkedList<Media> listMedia = getMediaListFromProjectUseCase.getMediaListFromProject();
        Project project = Project.getInstance(null, null, null);
        MediaTrack mediaTrack = project.getMediaTrack();
        LinkedList<Media> listMedia = mediaTrack.getItems();
        ArrayList<Media> items = new ArrayList<>(listMedia);

        if (items.size()>0) {
            removeVideoFromProjectUseCase.removeMediaItemsFromProject(items, this);
        }else{
            addVideoToProjectUseCase.addMediaItemsToProject(itemsToAdd, this);
        }
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess(MediaTrack mediaTrack) {
        addVideoToProjectUseCase.addMediaItemsToProject(itemsToAdd, this);
    }

    @Override
    public void onAddMediaItemToTrackError() {

    }

    @Override
    public void onAddMediaItemToTrackSuccess(MediaTrack mediaTrack) {
        galleryPagerView.navigate();
    }
}
