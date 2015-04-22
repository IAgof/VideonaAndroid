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

package com.videonasocialmedia.videona.model.entities.editor.exceptions;

/**
 * TODO doc
 * Created by jca on 30/3/15.
 */
public class NoMediaInTrackException extends Exception {
    public NoMediaInTrackException(String detailMessage) {
        super(detailMessage);
    }
    public NoMediaInTrackException() {
        super("");
    }
}
