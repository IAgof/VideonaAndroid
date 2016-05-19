package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.sources.MusicSource;

import java.util.List;

/**
 *
 */
public class GetMusicListUseCase {

    public List<Music> getAppMusic() {
        return new MusicSource().retrieveLocalMusic();
    }
}
