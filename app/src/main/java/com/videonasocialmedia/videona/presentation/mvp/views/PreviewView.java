package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public interface PreviewView {

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void updateVideoList();

    void showPreview(List<Video> movieList);

    void showError(String message);

    void updateVideoSize();

}
