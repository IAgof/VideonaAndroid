/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters.fx;

import com.videonasocialmedia.videona.eventbus.events.video.VideoAddedToTrackEvent;
import com.videonasocialmedia.videona.eventbus.events.video.VideosRemovedFromProjectEvent;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.views.ScissorsFxView;

import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class ScissorsFxPresenter {

    private ScissorsFxView scissorsFxView;

    public ScissorsFxPresenter(ScissorsFxView scissorsFxView) {
        this.scissorsFxView = scissorsFxView;
    }

    public void onResume() {
        EventBus.getDefault().register(this);
        if(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject() == 0)
            EventBus.getDefault().post(new VideosRemovedFromProjectEvent());
        else
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
    }

    public void onPause(){
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(VideosRemovedFromProjectEvent event){
        scissorsFxView.inhabilitateTrashButton();
    }

    public void onEvent(VideoAddedToTrackEvent event){
        scissorsFxView.habilitateTrashButton();
    }

}
