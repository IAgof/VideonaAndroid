package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 * Created by jliarte on 13/05/16.
 */
public interface ProjectPlayerView {
    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

}
