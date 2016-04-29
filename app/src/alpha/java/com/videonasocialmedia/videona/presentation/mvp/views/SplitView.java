package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 * Created by jca on 8/7/15.
 */
public interface SplitView {

    void showSplitBar(int videoDuration, int MarkerPosition);

    void refreshTimeTag(int duration);
}
