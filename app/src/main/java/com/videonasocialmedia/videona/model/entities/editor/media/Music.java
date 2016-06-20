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
    private String musicTitle;
    private String author;
    private int iconResourceId;

    public Music(int iconResourceId, String musicTitle, int musicResourceId, int colorResourceId, String author) {
        super("", "", "", musicTitle, "", 0, 0, null, null, null, null, null);
        this.musicResourceId = musicResourceId;
        this.colorResourceId = colorResourceId;
        this.musicTitle = musicTitle;
        this.iconResourceId = iconResourceId;
        this.author = author;
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

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String name) {
        this.musicTitle = name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getAuthor() {
        return author;
    }
}
