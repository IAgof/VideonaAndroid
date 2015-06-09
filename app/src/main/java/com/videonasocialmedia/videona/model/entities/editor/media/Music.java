/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor.media;

/**
 * @deprecated
 */
public class Music extends Audio {

    //TODO en el futuro no será un recurso sino que se obtendrá
    private int musicResourceId;
    private int colorResourceId;
    //TODO refactorizar nombre
    private String nameResourceId;
    private int iconResourceId;

    public Music(int iconResourceId, String nameResourceId, int musicResourceId, int colorResourceId) {
        super("", "", "", 0, 0, null, null);
        this.musicResourceId = musicResourceId;
        this.colorResourceId = colorResourceId;
        this.nameResourceId = nameResourceId;
        this.iconResourceId = iconResourceId;
    }

    public int getMusicResourceId() {
        return musicResourceId;
    }

    public void setMusicResourceId(int musicResourceId) {
        this.musicResourceId = musicResourceId;
    }

    public int getColorResourceId() {
        return colorResourceId;
    }

    public void setColorResourceId(int colorResourceId) {
        this.colorResourceId = colorResourceId;
    }

    public void setNameResourceId(String name) {
        this.nameResourceId = name;
    }

    public String getNameResourceId() {
        return nameResourceId;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }
}
