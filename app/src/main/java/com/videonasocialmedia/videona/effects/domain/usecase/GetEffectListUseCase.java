/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.usecase;

import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.repository.EffectProvider;

import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<Effect> getColorEffectList() {
        return EffectProvider.getColorEffectList();
    }

    public static List<Effect> getDistortionEffectList() {
        return EffectProvider.getDistortionEffectList();
    }

    public static List<Effect> getShaderEffectsList() {
        return EffectProvider.getShaderEffectList();
    }

    public static List<Effect> getOverlayEffectsList() {
        return EffectProvider.getOverlayFilterList();
    }

    public static Effect getOverlayEffectWolder() {
        return EffectProvider.getOverlayEffectWolder();
    }
}
