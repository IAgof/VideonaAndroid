package com.videonasocialmedia.videona.eventbus.events.music;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

/**
 * Created by jca on 29/7/15.
 */
public class ErrorAddingMusicToProjectEvent {
    public final Music music;

    public ErrorAddingMusicToProjectEvent(Music music) {
        this.music = music;
    }
}
