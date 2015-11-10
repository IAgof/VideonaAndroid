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
public class VideoResolution {

    private final int heigth;
    private final int width;

    public VideoResolution(Resolution resolution) {
        switch (resolution) {
            case HD1080:
                this.width = 1920;
                this.heigth = 1080;
                break;
            case HD4K:
                this.width = 3840;
                this.heigth = 2160;
                break;
            case HD720:
            default:
                this.width = 1280;
                this.heigth = 720;
        }

    }

    public int getHeight() {
        return heigth;
    }

    public int getWidth() {
        return width;
    }

    public enum Resolution {
        HD720, HD1080, HD4K
    }


}
