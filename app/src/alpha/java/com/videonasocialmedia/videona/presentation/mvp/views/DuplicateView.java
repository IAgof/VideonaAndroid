package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 8/7/15.
 */
public interface DuplicateView {

    void initDuplicateView(String videoPath);

    void playPreview();

    void pausePreview();

    void showPreview(List<Video> movieList);

    void showError(String message);

}
