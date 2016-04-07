package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.eventbus.events.VideoDurationModifiedEvent;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import de.greenrobot.event.EventBus;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
    public void modifyVideoStartTime(Video video, int starTime) {
        video.setFileStartTime(starTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
        EventBus.getDefault().post(new VideoDurationModifiedEvent(video));
    }

    public void modifyVideoFinishTime(Video video, int finishTime) {
        video.setFileStopTime(finishTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
        EventBus.getDefault().post(new VideoDurationModifiedEvent(video));
    }
}
