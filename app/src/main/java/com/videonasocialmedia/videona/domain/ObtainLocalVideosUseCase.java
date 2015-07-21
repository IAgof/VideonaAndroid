package com.videonasocialmedia.videona.domain;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.videona.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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

    class Pair implements Comparable {
        public long t;
        public File f;

        public Pair(File file) {
            f = file;
            t = file.lastModified();
        }

        public int compareTo(Object o) {
            long u = ((Pair) o).t;
            return t < u ? -1 : t == u ? 0 : 1;
        }
    }

    private ArrayList<Video> obtainVideosFromPath(String path) {
        ArrayList<Video> videos = null;
        File f = new File(path);
        File file[] = f.listFiles();

        if (file != null && file.length > 0) {
            videos = new ArrayList<>();
            Pair[] pairs = new Pair[file.length];
            for (int i = 0; i < file.length; i++)
                pairs[i] = new Pair(file[i]);

            Arrays.sort(pairs);
            for (int i = file.length - 1; i >= 0; i--) {
                file[i] = pairs[i].f;
                if (file[i].getName().endsWith(".mp4") && file[i].isFile())
                    videos.add(new Video(path + File.separator + file[i].getName(), 0, 0));
            }
        }

        return videos;
    }

}
