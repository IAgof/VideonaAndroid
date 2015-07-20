package com.videonasocialmedia.videona.domain;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jca on 20/5/15.
 */
public class ObtainLocalVideosUseCase {

    private final String LOG_TAG = "ObtainLocalVideos";

    public void obtainEditedVideos(OnVideosRetrieved listener) {
        ArrayList<Video> videos = obtainVideosFromPath(Constants.PATH_APP_EDITED);
        if (videos == null) {
            listener.onNoVideosRetrieved();
        } else {
            listener.onVideosRetrieved(videos);
        }
    }

    public void obtainRawVideos(OnVideosRetrieved listener) {
        ArrayList<Video> videos = obtainVideosFromPath(Constants.PATH_APP_MASTERS);
        if (videos == null) {
            listener.onNoVideosRetrieved();
        } else {
            listener.onVideosRetrieved(videos);
        }
    }


    private ArrayList<Video> obtainVideosFromPath(String path) {
        ArrayList<Video> videos = null;
        File f = new File(path);
        File file[] = f.listFiles();
        if (file != null && file.length > 0) {
            videos = new ArrayList<>();
            for (int i = file.length - 1; i >= 0; i--) {
                if (file[i].getName().endsWith(".mp4")) {
                    videos.add(new Video(path + File.separator + file[i].getName(), 0, 0));
                }
            }
        }
        return videos;
    }

}
