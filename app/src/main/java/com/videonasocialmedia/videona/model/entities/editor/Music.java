/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

// Hablar con Javi sobre esta clase

public class Music extends EditorElement {
    //TODO en el futuro no será un recurso sino que se obtendrá
    private int musicResourceId;
    private int colorResourceId;
    private String nameResourceId;

    public Music(int iconResourceId, String name, int musicResourceId, int colorResourceId) {

        this.iconResourceId = iconResourceId;
        this.nameResourceId = name;
        this.musicResourceId = musicResourceId;
        this.colorResourceId = colorResourceId;
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
    public void setNameResourceId(String name){
        this.nameResourceId = name;
    }
    public String getNameResourceId(){
        return nameResourceId;
    }
}



