package com.videonasocialmedia.videona.domain;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            videos = new ArrayList<>();

            Collections.sort(Arrays.asList(files), new Comparator<File>() {
                public int compare(File f1, File f2) {
                    long d1 = f1.lastModified();
                    long d2 = f2.lastModified();
                    return d1 > d2 ? 1 : d1 < d2 ? -1 : 0;
                }
            });
            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].getName().endsWith(".mp4") && files[i].isFile())
                    videos.add(new Video(path + File.separator + files[i].getName(), 0, 0));
            }

        }

        return videos;
    }

}
