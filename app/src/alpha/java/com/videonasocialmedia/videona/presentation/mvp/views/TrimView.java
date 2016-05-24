package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jca on 8/7/15.
 */
public interface TrimView {

    void showTrimBar(int leftMarkerPosition, int RightMarkerPosition);

    void refreshDurationTag(int duration);

    void refreshStartTimeTag(int startTime);

    void refreshStopTimeTag(int stopTime);

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void showPreview(List<Video> movieList);

    void showError(String message);
}
