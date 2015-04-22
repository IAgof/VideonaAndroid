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
package com.videonasocialmedia.videona.model.entities.editor.effects;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.utils.Size;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * A type of effect than consist on overlay an image, drawing or video to a track. It defines size
 * and coordinates for the frame in which it will be applied.
 *
 * Created by jca on 30/3/15.
 */
public class Overlay extends Effect {

    /**
     * Frame sizes in pixels related standards screes.
     */
    protected Size frameSize;

    /**
     * Coordinates related to the screen in which the frame overlay has to be draw.
     */
    protected Coordinates coords;

    /**
     *
     * @param overlayType - Overlay unique identifier.
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Overlay(String overlayType, Coordinates coords, Size frameSize, int layer, long startTime,
                   long duration, User author) {
        super(overlayType, layer, startTime, duration, author);
        this.frameSize = frameSize;
        this.coords = coords;
    }

    /**
     *
     * @param overlayType - Overlay unique identifier.
     * @see com.videonasocialmedia.videona.model.entities.editor.effects.Effect
     */
    public Overlay(String overlayType, Coordinates coords, Size frameSize, int layer, long startTime,
                   long duration, String authorName) {
        super(overlayType, layer, startTime, duration, authorName);
        this.frameSize = frameSize;
        this.coords = coords;
    }


    //apply methods
    /**
     * TODO
     * @param target - the media file to be modified by the effect.
     */
    @Override
    public void doTheMagic(Media target) {
    }

    /**
     * TODO
     */
    @Override
    public void preview() {
    }


    //getters & setters
    public Size getFrameSize() {
        return frameSize;
    }
    public void setFrameSize(Size frameSize) {
        this.frameSize = frameSize;
    }
    public Coordinates getCoords() {
        return coords;
    }
    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    //TODO es bastante probable que esto se deba mover a un utils. Y se haga algo más que esto.
    protected class Coordinates {
        int x, y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
