package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 6/7/15.
 */
public interface VideoTimeLineView {
    void showVideoList(List<Video> videoList);
}
