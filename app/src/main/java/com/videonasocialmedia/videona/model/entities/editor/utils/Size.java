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
        HD720, HD1080, HD4K
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
            case HD720:
                this.width = 1280;
                this.heigth = 720;
                break;
            case HD1080:
                this.width = 1920;
                this.heigth = 1080;
                break;
            case HD4K:
                this.width = 3840;
                this.heigth = 2160;
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
