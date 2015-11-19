package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnReorderMediaListener;

/**
 * Created by jca on 7/7/15.
 */
public class ReorderMediaItemUseCase {

    public void moveMediaItem(Media media, int toPositon, OnReorderMediaListener listener){
        Project project= Project.getInstance(null,null,null);
        MediaTrack videoTrack= project.getMediaTrack();
        try {
            videoTrack.moveItemTo(toPositon,media);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
        }
    }
}
