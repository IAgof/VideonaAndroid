package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;

/**
 * Created by jca on 29/5/15.
 */
public interface ModifyVideoDurationlistener {

    void onVideoDurationModified(Video modifiedVideo);
}
