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

        cameraEffects.add(new CameraEffectFxList("Normal", R.drawable.common_filter_fx_fx0_none_normal, R.drawable.common_filter_fx_fx0_none_pressed));
        cameraEffects.add(new CameraEffectFxList("Fisheye", R.drawable.common_filter_fx_fx1_fisheye_normal, R.drawable.common_filter_fx_fx1_fisheye_pressed));
        cameraEffects.add(new CameraEffectFxList("Stretch", R.drawable.common_filter_fx_fx2_stretch_normal, R.drawable.common_filter_fx_fx2_stretch_pressed));
        cameraEffects.add(new CameraEffectFxList("Dent", R.drawable.common_filter_fx_fx3_dent_normal, R.drawable.common_filter_fx_fx3_dent_pressed));
        cameraEffects.add(new CameraEffectFxList("Mirror", R.drawable.common_filter_fx_fx4_mirror_normal, R.drawable.common_filter_fx_fx4_mirror_pressed));
        cameraEffects.add(new CameraEffectFxList("Squeeze", R.drawable.common_filter_fx_fx5_squeeze_normal, R.drawable.common_filter_fx_fx5_squeeze_pressed));
        cameraEffects.add(new CameraEffectFxList("Tunnel", R.drawable.common_filter_fx_fx6_tunnel_normal, R.drawable.common_filter_fx_fx6_tunnel_pressed));
        cameraEffects.add(new CameraEffectFxList("Twirl", R.drawable.common_filter_fx_fx7_twirl_normal, R.drawable.common_filter_fx_fx7_twirl_pressed));
        cameraEffects.add(new CameraEffectFxList("Bulge", R.drawable.common_filter_fx_fx8_bulge_normal, R.drawable.common_filter_fx_fx8_bulge_pressed));


        return cameraEffects;
    }


}
