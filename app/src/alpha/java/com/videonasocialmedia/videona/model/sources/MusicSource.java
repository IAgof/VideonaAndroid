package com.videonasocialmedia.videona.model.sources;

import android.content.Context;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MusicSource {

    Context context = VideonaApplication.getAppContext();

    private List<Music> localMusic = new ArrayList();

    public List<Music> retrieveLocalMusic() {
        if (localMusic.size() == 0)
            populateLocalMusic();
        return localMusic;
    }

    private void populateLocalMusic() {

        localMusic.add(new Music(R.drawable.imagebutton_music_background_rock, "audio_rock", R.raw.audio_rock, R.color.rock, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_ambient, "audio_ambiental", R.raw.audio_ambiental, R.color.ambient, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_jazz, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.jazz, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_folk, "audio_folk", R.raw.audio_folk, R.color.folk, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_birthday, "birthday", R.raw.audio_birthday, R.color.birthday, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_hiphop, "audio_hiphop", R.raw.audio_hiphop, R.color.hiphop, "author"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_classic, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.classic, "author"));
    }

}
