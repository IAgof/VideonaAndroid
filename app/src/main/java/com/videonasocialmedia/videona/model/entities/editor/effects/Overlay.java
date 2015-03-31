/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor.effects;

import com.videonasocialmedia.videona.model.entities.editor.Size;

/**
 * Created by jca on 30/3/15.
 */
public class Overlay extends Effect {

    Size size;
    Coord coord;

    public Overlay(long duration, long startTime, Size size, Coord coord) {
        super(duration, startTime);
        this.size = size;
        this.coord = coord;
    }

    class Coord {
        int x, y;

        Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
