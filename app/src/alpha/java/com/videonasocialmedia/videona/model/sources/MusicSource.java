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

        localMusic.add(new Music(R.drawable.imagebutton_music_background_bollywood, "In Tune", R.raw.audio_pride, R.color.pride, "Kellee Maize"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_bollywood, "Vadodora", R.raw.audio_bollywood, R.color.bollywood, "Kevin Macleod"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_rock, "Airplane Mod Instrumental", R.raw.audio_rock, R.color.rock, "James Woodward"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_ambient, "Impact Prelude", R.raw.audio_ambiental, R.color.ambient, "Kevin Macleod"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_jazz, "Monkeys Spinning Monkeys", R.raw.audio_clasica_flauta, R.color.jazz, "Kevin Macleod"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_folk, "Don't Close Your Eyes", R.raw.audio_folk, R.color.folk, "Josh Woodward"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_birthday, "Super Psyched for Your Birthday", R.raw.audio_birthday, R.color.birthday, "The Danimals"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_hiphop, "I Dunno", R.raw.audio_hiphop, R.color.hiphop, "Grapes"));
        localMusic.add(new Music(R.drawable.imagebutton_music_background_classic, "The Last Slice of Pecan Pie", R.raw.audio_clasica_piano, R.color.classic, "Josh Woodward"));
    }

}
