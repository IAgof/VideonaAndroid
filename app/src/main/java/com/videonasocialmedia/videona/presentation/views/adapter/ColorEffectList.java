package com.videonasocialmedia.videona.presentation.views.adapter;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import com.videonasocialmedia.videona.presentation.views.CameraPreview;
import com.videonasocialmedia.videona.utils.ConfigUtils;
import com.videonasocialmedia.videona.utils.utils.ConstantsUtils;

import java.util.ArrayList;

public class ColorEffectList {


    public static ArrayList<String> getColorEffectList() {

        ArrayList<String> colorEffects = new ArrayList<String>();

        // Color Effects constants in Android_L
        if (ConfigUtils.isAndroidL) {

            colorEffects.add(ConstantsUtils.COLOR_EFFECT_NONE);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_MONO);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_NEGATIVE);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_SOLARIZE);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_SEPIA);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_POSTERIZE);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_WHITEBOARD);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_BLACKBOARD);
            colorEffects.add(ConstantsUtils.COLOR_EFFECT_AQUA);

        } else {

            // Color Effects in pre_Android_L depends on camera getSupportedColorEffects
            for (String effect : CameraPreview.colorEffects) {

                colorEffects.add(effect);
            }

        }

        return colorEffects;
    }

}
