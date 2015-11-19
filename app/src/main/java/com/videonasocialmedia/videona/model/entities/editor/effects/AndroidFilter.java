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

import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * This class is an interface to use filters provided by android software.
 * Created by dfa on 7/4/15.
 * TODO hay que pensarsela entera. Es una ñapa mientras hacemos TODOS los filtros con opengl.
 */
public class AndroidFilter extends Filter {

    public AndroidFilter(String identifier, String iconPath, String type, long startTime,
                         long duration, License license, User author) {
        super(identifier, iconPath, type, startTime, duration, license, author);
    }

    public AndroidFilter(String iconPath, String selectedIconPath, String identifier, String type,
                         long startTime, long duration, int layer, User author, License license) {
        super(iconPath, selectedIconPath, identifier, type, startTime, duration, layer, author, license);
    }
}
