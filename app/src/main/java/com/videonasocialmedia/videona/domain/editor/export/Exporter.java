package com.videonasocialmedia.videona.domain.editor.export;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.io.IOException;
import java.util.List;

/**
 * Created by jca on 27/5/15.
 */
public interface Exporter {
    Video trimVideo(Video videoToTrim, String outputPath) throws IOException;
    Video mergeVideos(List<Video>videoList);
    Video addMusicToVideo(Video video, Music music, String outputPath) throws IOException;

    //Falta meter la resolución de salida
    Video transcodeVideo (Video video);
}
