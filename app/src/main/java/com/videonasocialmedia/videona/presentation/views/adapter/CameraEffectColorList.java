/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import com.videonasocialmedia.videona.R;

import java.util.LinkedList;

public class CameraEffectColorList {

    private String nameCameraEffectColorId;
    private int iconCameraEffectColorId;
    private int iconCameraEffectColorIdPressed;

    public CameraEffectColorList(String nameResourceId, int iconResourceId, int iconResourceIdPressed) {

        this.nameCameraEffectColorId = nameResourceId;
        this.iconCameraEffectColorId = iconResourceId;
        this.iconCameraEffectColorIdPressed = iconResourceIdPressed;
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

        cameraEffectColor.add(new CameraEffectColorList("none", R.drawable.common_filter_ad0_none_normal, R.drawable.common_filter_ad0_none_pressed));
        cameraEffectColor.add(new CameraEffectColorList("aqua",  R.drawable.common_filter_ad1_aqua_normal,  R.drawable.common_filter_ad1_aqua_pressed));
        cameraEffectColor.add(new CameraEffectColorList("posterize_bw",  R.drawable.common_filter_ad2_postericebw_normal,  R.drawable.common_filter_ad2_postericebw_pressed));
        cameraEffectColor.add(new CameraEffectColorList("emboss",  R.drawable.common_filter_ad3_emboss_normal,  R.drawable.common_filter_ad3_emboss_pressed));
        cameraEffectColor.add(new CameraEffectColorList("mono",  R.drawable.common_filter_ad4_mono_normal,  R.drawable.common_filter_ad4_mono_pressed));
        cameraEffectColor.add(new CameraEffectColorList("negative",  R.drawable.common_filter_ad5_negative_normal,  R.drawable.common_filter_ad5_negative_pressed));
        cameraEffectColor.add(new CameraEffectColorList("night",  R.drawable.common_filter_ad6_green_normal,  R.drawable.common_filter_ad6_green_pressed));
        cameraEffectColor.add(new CameraEffectColorList("posterize",  R.drawable.common_filter_ad7_posterize_normal,  R.drawable.common_filter_ad7_posterize_pressed));
        cameraEffectColor.add(new CameraEffectColorList("sepia",  R.drawable.common_filter_ad8_sepia_normal,  R.drawable.common_filter_ad8_sepia_pressed));

        return cameraEffectColor;
    }

}
