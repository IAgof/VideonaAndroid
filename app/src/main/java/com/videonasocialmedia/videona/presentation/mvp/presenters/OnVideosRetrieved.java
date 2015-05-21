package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 18/5/15.
 */
public interface OnVideosRetrieved {

    void onVideosRetrieved(List<Video> videoList);

    void onNoVideosRetrieved();
}
