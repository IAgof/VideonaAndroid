/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.model;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class ShaderEffect extends Effect {

    private final int resourceId;

    public ShaderEffect(String identifier, String name, int iconId, int resourceId,
                        String effectType) {
        super(identifier, name, iconId, effectType, true);
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
