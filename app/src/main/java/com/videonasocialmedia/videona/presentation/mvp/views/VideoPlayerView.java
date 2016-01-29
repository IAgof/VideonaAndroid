package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 * Created by jca on 11/12/15.
 */
public interface VideoPlayerView {

    void playVideo();

    void pauseVideo();

    void seekTo(int millisecond);
}
