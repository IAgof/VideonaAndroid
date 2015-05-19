package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryPresenter implements OnVideosRetrieved {

    private VideoGalleryView galleryView;

    public VideoGalleryPresenter(VideoGalleryView galleryView) {
        this.galleryView = galleryView;
    }


    @Override
    public void onVideosRetrieved(List<Video> videoList) {

        if (galleryView.isTheListEmpty())
            galleryView.showVideos(videoList);
//        else
//            galleryView.appendVideos(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO show error in view
    }


    public void start() {
        if (galleryView.isTheListEmpty()) {
            //TODO llamar al caso de uso para obtener los videos
            createVideoList();
        }
    }


    public void stop() {
    }


    //temporal para poder probar la interfaz
    private void createVideoList(){
        ArrayList <Video> videoList= new ArrayList<>();
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_3.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_23.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_t.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_1.mp4", 0));
        videoList.add(new Video(Constants.PATH_APP_MASTERS+ File.separator+"video_2.mp4", 0));
        onVideosRetrieved(videoList);
    }
}
