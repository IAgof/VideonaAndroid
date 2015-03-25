/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor.media.audio;

import android.net.Uri;

import com.videonasocialmedia.videona.model.entities.editor.EditorElement;

/**
 * Created by jca on 25/3/15.
 */
public class Music extends EditorElement{

    //TODO en el futuro no será un recurso sino que se obtendrá
    private int musicResourceId;


    public Music(int iconResourceId, String name, int musicResourceId){
        this.iconResourceId=iconResourceId;
        this.name=name;
        this.musicResourceId=musicResourceId;
    }

    public int getMusicResourceId() {
        return musicResourceId;
    }

    public void setMusicResourceId(int musicResourceId) {
        this.musicResourceId = musicResourceId;
    }

}
