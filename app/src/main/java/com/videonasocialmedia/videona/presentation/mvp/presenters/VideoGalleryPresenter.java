package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;

import java.util.List;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryPresenter implements OnVideosRetrieved {

    public static final int MASTERS_FOLDER = 0;
    public static final int EDITED_FOLDER = 1;

    private VideoGalleryView galleryView;
    private ObtainLocalVideosUseCase obtainLocalVideosUseCase;

    public VideoGalleryPresenter(VideoGalleryView galleryView) {
        this.galleryView = galleryView;
        obtainLocalVideosUseCase = new ObtainLocalVideosUseCase();
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {

        //if (galleryView.isTheListEmpty())
        galleryView.showVideos(videoList);
//        else
//            galleryView.appendVideos(itemsToAdd);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO show error in view
    }


    public void start() {

    }

    public void obtainVideos(int folder) {
        switch (folder) {
            case MASTERS_FOLDER:
                obtainLocalVideosUseCase.obtainRawVideos(this);
                break;
            case EDITED_FOLDER:
            default:
                obtainLocalVideosUseCase.obtainEditedVideos(this);
                break;
        }
    }


    public void stop() {
    }

}
