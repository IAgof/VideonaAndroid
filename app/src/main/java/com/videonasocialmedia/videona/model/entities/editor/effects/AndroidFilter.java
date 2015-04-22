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

import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * This class is an interface to use filters provided by android software.
 * Created by dfa on 7/4/15.
 * TODO hay que pensarsela entera. Es una ñapa mientras hacemos TODOS los filtros con opengl.
 */
public class AndroidFilter extends Filter {

    public AndroidFilter(String filterType, int layer, long startTime, long duration, User author) {
        super(filterType, layer, startTime, duration, author);
    }

    public AndroidFilter(String filterType, int layer, long startTime, long duration, String authorName) {
        super(filterType, layer, startTime, duration, authorName);
    }
}
