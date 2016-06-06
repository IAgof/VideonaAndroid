package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jliarte on 13/05/16.
 */
public interface VideonaPlayerView {
    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void setMusic(Music music);

    void bindVideoList(List<Video>videoList);

}
