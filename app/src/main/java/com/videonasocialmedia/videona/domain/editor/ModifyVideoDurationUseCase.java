package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.ModifyVideoDurationlistener;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
    public void modifyVideoStartTime(Video video, int starTime, ModifyVideoDurationlistener listener) {
        video.setFileStartTime(starTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
        listener.onVideoDurationModified(video);
    }

    public void modifyVideoFinishTime(Video video, int finishTime, ModifyVideoDurationlistener listener) {
        video.setFileStopTime(finishTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
        listener.onVideoDurationModified(video);
    }
}
