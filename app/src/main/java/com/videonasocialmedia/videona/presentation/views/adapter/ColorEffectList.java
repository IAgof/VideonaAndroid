/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.hardware.Camera;
import android.util.Log;

import com.videonasocialmedia.videona.utils.Constants;

import java.util.ArrayList;

public class ColorEffectList {

    public static ArrayList<String> getColorEffectList(Camera camera) {

        ArrayList<String> colorEffects = new ArrayList<String>();


        /*

        // Color Effects constants in Android_L
        if (ConfigUtils.isAndroidL) {

            colorEffects.add(Constants.COLOR_EFFECT_NONE);
            colorEffects.add(Constants.COLOR_EFFECT_MONO);
            colorEffects.add(Constants.COLOR_EFFECT_NEGATIVE);
            colorEffects.add(Constants.COLOR_EFFECT_SOLARIZE);
            colorEffects.add(Constants.COLOR_EFFECT_SEPIA);
            colorEffects.add(Constants.COLOR_EFFECT_POSTERIZE);
            colorEffects.add(Constants.COLOR_EFFECT_WHITEBOARD);
            colorEffects.add(Constants.COLOR_EFFECT_BLACKBOARD);
            colorEffects.add(Constants.COLOR_EFFECT_AQUA);

        } else {

            // Color Effects in pre_Android_L depends on camera getSupportedColorEffects
            for (String effect : CameraPreview.colorEffects) {

                colorEffects.add(effect);
            }

        }

        */


        // Color Effects in pre_Android_L depends on camera getSupportedColorEffects
        // for (String effect : CameraPreview.colorEffects) {
        for (String effect : camera.getParameters().getSupportedColorEffects()) {

            colorEffects.add(effect);

        }

        return colorEffects;
        // return sortColorEffectList();
    }


    /**
     * Gets an instance of the camera object
     * @param cameraId
     * @return
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            Log.d("DEBUG", "Camera did not open");
        }
        return c;
    }

    /**
     * Sort Color Effect List
     * <p/>
     * List sort by filter number, AD0, AD5, ... Design
     *
     * @return ArrayList
     */

    public static ArrayList<String> sortColorEffectList(ArrayList<String> colorEffects) {

        ArrayList<String> colorEffectsSorted = new ArrayList<String>();

        //   ArrayList<String> colorEffects = getColorEffectList();


        for (String effect : colorEffects) {

            Log.d("RecordActivity", " colorEffects " + effect);

            if (Constants.COLOR_EFFECT_NONE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NONE);

            }

        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_AQUA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_AQUA);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_BLACKBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_BLACKBOARD);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_EMBOSS.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_EMBOSS);

            }
        }

        for (String effect : colorEffects) {
            if (Constants.COLOR_EFFECT_MONO.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_MONO);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_NEGATIVE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEGATIVE);

            }
        }

        for (String effect : colorEffects) {
            if (Constants.COLOR_EFFECT_NEON.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_NEON);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_POSTERIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_POSTERIZE);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_SEPIA.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SEPIA);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_SKETCH.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SKETCH);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_SOLARIZE.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_SOLARIZE);

            }
        }

        for (String effect : colorEffects) {

            if (Constants.COLOR_EFFECT_WHITEBOARD.compareTo(effect) == 0) {
                colorEffectsSorted.add(Constants.COLOR_EFFECT_WHITEBOARD);

            }
        }


        return colorEffectsSorted;
    }

}
