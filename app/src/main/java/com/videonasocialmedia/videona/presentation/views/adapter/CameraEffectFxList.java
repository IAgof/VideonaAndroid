/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import com.videonasocialmedia.videona.R;

import java.util.LinkedList;

public class CameraEffectFxList {


    private String nameCameraFxId;
    private int iconCameraFxId;
    private int iconCameraFxIdPressed;

    public CameraEffectFxList(String nameResourceId, int iconResourceId, int iconResourceIdPressed) {

        this.nameCameraFxId = nameResourceId;
        this.iconCameraFxId = iconResourceId;
        this.iconCameraFxIdPressed = iconResourceIdPressed;
    }


    public void setNameResourceId(String name) {
        this.nameCameraFxId = name;
    }

    public String getNameResourceId() {
        return nameCameraFxId;
    }

    public int getIconCameraFxId() {
        return iconCameraFxId;
    }

    public void setIconCameraFxId(int iconCameraFxId) {
        this.iconCameraFxId = iconCameraFxId;
    }


    public int getIconCameraFxIdPressed() {
        return iconCameraFxIdPressed;
    }

    public void setIconCameraFxIdPressed(int iconCameraFxIdPressed) {
        this.iconCameraFxIdPressed = iconCameraFxIdPressed;
    }

    public static LinkedList<CameraEffectFxList> getCameraEffectList() {

        LinkedList<CameraEffectFxList> cameraEffects = new LinkedList<>();

        cameraEffects.add(new CameraEffectFxList("Normal", R.drawable.common_filter_fx_normal_fx1, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Fisheye", R.drawable.common_filter_fx_fisheye_fx2, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Stretch", R.drawable.common_filter_fx_stretch_fx3, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Dent", R.drawable.common_filter_fx_dent_fx4, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Mirror", R.drawable.common_filter_fx_mirror_fx5, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Squeeze", R.drawable.common_filter_fx_squeeze_fx6, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Tunnel", R.drawable.common_filter_fx_tunnel_fx7, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Twirl", R.drawable.common_filter_fx_twirl_fx8, R.drawable.gatito_rules_pressed));
        cameraEffects.add(new CameraEffectFxList("Bulge", R.drawable.common_filter_fx_bulge_fx9, R.drawable.gatito_rules_pressed));


        return cameraEffects;
    }


}
