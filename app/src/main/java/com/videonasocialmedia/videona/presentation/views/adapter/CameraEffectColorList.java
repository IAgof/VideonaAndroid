/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.hardware.Camera;
import android.util.Log;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class CameraEffectColorList {

    private String nameCameraEffectColorId;
    private int iconCameraEffectColorId;
    private int iconCameraEffectColorIdPressed;
    private int orderCameraEffectColorList;

    public CameraEffectColorList(String nameResourceId, int iconResourceId, int iconResourceIdPressed, int orderCameraEffectColorList) {

        this.nameCameraEffectColorId = nameResourceId;
        this.iconCameraEffectColorId = iconResourceId;
        this.iconCameraEffectColorIdPressed = iconResourceIdPressed;
        this.orderCameraEffectColorList = orderCameraEffectColorList;
    }

    public void setNameResourceId(String name) {
        this.nameCameraEffectColorId = name;
    }

    public String getNameResourceId() {
        return nameCameraEffectColorId;
    }

    public int getIconCameraEffectColorId() {
        return iconCameraEffectColorId;
    }

    public void setIconCameraEffectColorId(int iconCameraFxId) {
        this.iconCameraEffectColorId = iconCameraFxId;
    }


    public int getIconCameraEffectColorIdPressed() {
        return iconCameraEffectColorIdPressed;
    }

    public void setIconCameraEffectColorIdPressed(int iconCameraEffectColorIdPressed) {
        this.iconCameraEffectColorIdPressed = iconCameraEffectColorIdPressed;
    }

    public static LinkedList<CameraEffectColorList> getCameraEffectColorList() {

        LinkedList<CameraEffectColorList> cameraEffectColor = new LinkedList<>();


        cameraEffectColor.add(new CameraEffectColorList("none", R.drawable.common_filter_none_ad0_normal, R.drawable.common_filter_none_ad0_pressed, 0));
        cameraEffectColor.add(new CameraEffectColorList("aqua",  R.drawable.common_filter_aqua_ad1_normal,  R.drawable.common_filter_aqua_ad1_pressed, 1));
        cameraEffectColor.add(new CameraEffectColorList("blackboard",  R.drawable.common_filter_blackboard_ad2_normal,  R.drawable.common_filter_blackboard_ad2_pressed, 2));
        cameraEffectColor.add(new CameraEffectColorList("emboss",  R.drawable.common_filter_emboss_ad3_normal,  R.drawable.common_filter_emboss_ad3_pressed, 3));
        cameraEffectColor.add(new CameraEffectColorList("mono",  R.drawable.common_filter_mono_ad4_normal,  R.drawable.common_filter_mono_ad4_pressed, 4));
        cameraEffectColor.add(new CameraEffectColorList("negative",  R.drawable.common_filter_negative_ad5_normal,  R.drawable.common_filter_negative_ad5_pressed, 5));
        cameraEffectColor.add(new CameraEffectColorList("neon",  R.drawable.common_filter_neon_ad6_normal,  R.drawable.common_filter_neon_ad6_pressed, 6));
        cameraEffectColor.add(new CameraEffectColorList("posterize",  R.drawable.common_filter_posterize_ad7_normal,  R.drawable.common_filter_posterize_ad7_pressed, 7));
        cameraEffectColor.add(new CameraEffectColorList("sepia",  R.drawable.common_filter_sepia_ad8_normal,  R.drawable.common_filter_sepia_ad8_pressed, 8));
        cameraEffectColor.add(new CameraEffectColorList("sketch",  R.drawable.common_filter_sketch_ad9_normal,  R.drawable.common_filter_sketch_ad9_pressed, 9));
        cameraEffectColor.add(new CameraEffectColorList("solarize",  R.drawable.common_filter_solarize_ad10_normal,  R.drawable.common_filter_solarize_ad10_pressed, 10));
        cameraEffectColor.add(new CameraEffectColorList("whiteboard", R.drawable.common_filter_whiteboard_ad11_normal, R.drawable.common_filter_whiteboard_ad11_pressed, 11));

        return cameraEffectColor;
    }


    public static LinkedList<CameraEffectColorList> getCameraEffectColorListSorted(Camera camera) {

        LinkedList<CameraEffectColorList>cameraEffectColorLists = getCameraEffectColorList();

        LinkedList<CameraEffectColorList> cameraEffectColorSorted = new LinkedList<>();

        for(int i=0; i<cameraEffectColorLists.size(); i++){

            for (String effect : camera.getParameters().getSupportedColorEffects()) {
                if(effect.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorSorted.add(cameraEffectColorLists.get(i));
                }
            }
        }


        Collections.sort(cameraEffectColorSorted, new Comparator<CameraEffectColorList>() {
            @Override
            public int compare(CameraEffectColorList lhs, CameraEffectColorList rhs) {
                return lhs.orderCameraEffectColorList - rhs.orderCameraEffectColorList;
            }
        });


        return cameraEffectColorSorted;
    }


    public static int getResourceDrawableColorEffect(String colorEffectName, boolean isPressed) {

        String colorName = " ";

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_AQUA) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_aqua_ad1_normal;
            } else {
                return R.drawable.common_filter_aqua_ad1_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_BLACKBOARD) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_blackboard_ad2_normal;
            } else {
                return R.drawable.common_filter_blackboard_ad2_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_MONO) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_mono_ad4_normal;
            } else {
                return R.drawable.common_filter_mono_ad4_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEGATIVE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_negative_ad5_normal;
            } else {
                return R.drawable.common_filter_negative_ad5_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NONE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_none_ad0_normal;
            } else {
                return R.drawable.common_filter_none_ad0_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_POSTERIZE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_posterize_ad7_normal;
            } else {
                return R.drawable.common_filter_posterize_ad7_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SEPIA) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_sepia_ad8_normal;
            } else {
                return R.drawable.common_filter_sepia_ad8_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_WHITEBOARD) == 0) {
            if (!isPressed) {
                return R.drawable.common_filter_whiteboard_ad11_normal;
            } else {
                return R.drawable.common_filter_whiteboard_ad11_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SOLARIZE) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_solarize_ad10_normal;
            } else {
                return R.drawable.common_filter_solarize_ad10_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_EMBOSS) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_emboss_ad3_normal;
            } else {
                return R.drawable.common_filter_emboss_ad3_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_SKETCH) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_sketch_ad9_normal;
            } else {
                return R.drawable.common_filter_sketch_ad9_pressed;
            }
        }

        if (colorEffectName.compareTo(Constants.COLOR_EFFECT_NEON) == 0) {

            if (!isPressed) {
                return R.drawable.common_filter_neon_ad6_normal;
            } else {
                return R.drawable.common_filter_neon_ad6_pressed;
            }
        }

        return 0;
    }

    /**
     * Sort Color Effect List
     * <p/>
     * List sort by filter number, AD0, AD5, ... Design
     *
     * @return ArrayList
     */

    public static LinkedList<CameraEffectColorList> sortCameraEffectColorList(LinkedList<CameraEffectColorList>cameraEffectColorLists) {

        LinkedList<CameraEffectColorList> cameraEffectColorListSorted = new LinkedList<>();

        ListIterator it = cameraEffectColorLists.listIterator();

        while(it.hasNext()){

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_NONE.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_AQUA.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_BLACKBOARD.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_EMBOSS.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_NEGATIVE.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_NEON.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_POSTERIZE.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_SEPIA.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_SKETCH.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_SOLARIZE.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

            for(int i = 0; i < cameraEffectColorLists.size(); i++){
                if(Constants.COLOR_EFFECT_WHITEBOARD.compareTo(cameraEffectColorLists.get(i).getNameResourceId()) == 0){
                    cameraEffectColorListSorted.add(cameraEffectColorLists.get(i));
                }
            }

        }


        return cameraEffectColorListSorted;
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
