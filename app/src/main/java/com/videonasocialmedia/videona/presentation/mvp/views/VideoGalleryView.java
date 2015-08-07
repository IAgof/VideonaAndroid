package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 14/5/15.
 */
public interface VideoGalleryView {


    void showLoading();

    void hideLoading();

    void showVideos(List<Video> videoList);

    boolean isTheListEmpty();

    void appendVideos(List<Video> movieList);

    void showVideoTimeline();

}
