package com.videonasocialmedia.videona.eventbus.events.music;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

/**
 * Created by jca on 29/7/15.
 */
public class MusicAddedToProjectEvent {
    public final Music music;

    public MusicAddedToProjectEvent(Music music) {
        this.music = music;
    }
}
