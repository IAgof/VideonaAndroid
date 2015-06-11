/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */

package com.videonasocialmedia.videona.model.entities.editor.utils;

/**
 * Created by jca on 30/3/15.
 */
public class Size {

    public static enum Resolution {
        hd1080, hd720, sd480
    }

    private int heigth;
    private int width;

    public Size(int width, int heigth) {

        //TODO check aspect ratio is 16:9
        this.heigth = heigth;
        this.width = width;
    }

    public Size(Resolution resolution) {
        switch (resolution) {
            case hd1080:
                this.width = 1920;
                this.heigth = 1080;
                break;
            case hd720:
                this.width = 1280;
                this.heigth = 720;
                break;
            case sd480:
                this.width = 854;
                this.heigth = 480;
                break;
            default:
                this.width = 1280;
                this.heigth = 720;
        }

    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
