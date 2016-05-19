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

        localMusic.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_birthday_normal, "birthday", R.raw.audio_birthday, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop, R.color.colorAccent, "author"));
        localMusic.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.colorAccent, "author"));
    }

}
