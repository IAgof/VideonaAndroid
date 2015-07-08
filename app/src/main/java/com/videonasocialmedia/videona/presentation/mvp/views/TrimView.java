package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 * Created by jca on 8/7/15.
 */
public interface TrimView {

    void showTrimBar(int videoDuration, int leftMarkerPosition, int RightMarkerPosition);

    void createAndPaintVideoThumbs(String videoPath, int videoDuration);

    void refreshDurationTag(int duration);
    void refreshStartTimeTag(int startTime);
    void refreshStopTimeTag(int stopTime);

}
