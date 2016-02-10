package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.util.Log;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.videona.eventbus.events.video.VideosRetrievedFromProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoTimeLineView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by jca on 6/7/15.
 */
public class VideoTimeLinePresenter implements OnVideosRetrieved, OnReorderMediaListener {

    private VideoTimeLineView timelineView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ReorderMediaItemUseCase reorderMediaItemUseCase;

    public VideoTimeLinePresenter(VideoTimeLineView timelineView) {
        this.timelineView = timelineView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        reorderMediaItemUseCase= new ReorderMediaItemUseCase();
    }

    @Deprecated
    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        timelineView.showVideoList(videoList);
    }

    @Deprecated
    @Override
    public void onNoVideosRetrieved() {
        //TODO show error in view??
        Log.d("VIDEOTIMELINEPRESENTER", "No videos retrieved from project");
    }

    public void onEvent(VideosRetrievedFromProjectEvent event){
        if (event.videoList!=null)
            timelineView.showVideoList(event.videoList);
        //TODO if there is no list notify the user
    }

    public void start() {
        EventBus.getDefault().register(this);
        obtainVideos();
    }

    public void pause() {
        EventBus.getDefault().unregister(this);
    }

    public void moveItem(Media videoToMove, int toPositon){
        reorderMediaItemUseCase.moveMediaItem(videoToMove,toPositon,this);
    }

    @Override
    public void onMediaReordered(Media media, int newPosition) {
        //If everything was right the UI is already updated since the user did the reordering
    }

    @Override
    public void onErrorReorderingMedia() {
        //The reordering went wrong so we ask the project for the actual video list
        obtainVideos();
    }

    public void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }
}
