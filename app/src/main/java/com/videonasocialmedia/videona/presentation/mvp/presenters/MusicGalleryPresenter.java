/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicGalleryView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show the music gallery.
 */
public class MusicGalleryPresenter implements OnMusicRetrievedListener {

    private MusicGalleryView galleryView;
    private List<Music> musicList = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param galleryView the view of the music in the editor activity
     */
    public MusicGalleryPresenter(MusicGalleryView galleryView) {
        this.galleryView = galleryView;
    }

    @Override
    public void onMusicRetrieved(List<Music> musicList) {
        if (galleryView.isTheListEmpty()) {
            galleryView.showMusic(musicList);
        }
        /*
        else {
            //galleryView.appendMusic(musicList);
        }
        */
    }

    @Override
    public void onNoMusicRetrieved() {
        //TODO show error in view
    }

    /**
     * Creates the list of the available songs if not exists or reload it if exists
     */
    public void start() {
        if (galleryView.isTheListEmpty()) {
            //TODO llamar al caso de uso para obtener las canciones
            createMusicList();
        } else {
            galleryView.reloadMusic(musicList);
        }
    }

    public void stop() {
    }

    /**
     * temporal para poder probar la interfaz
     */
    private void createMusicList() {
        musicList.add(new Music(R.drawable.activity_music_icon_remove_normal, "Remove", -1, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_birthday_normal, "birthday", R.raw.audio_birthday, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop, R.color.videona_black_1));
        musicList.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano, R.color.videona_black_1));
        onMusicRetrieved(musicList);
    }
}
