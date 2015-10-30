/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.videona.R;

import java.util.ArrayList;
import java.util.List;

public class CameraEffectFx {


    private final String name;
    private final String iconResourceName;
    private final int iconResourceId;
    private final int iconPressedResourceId;
    //filterID is tight coupled with the camera framework. It's necessary to rewrite the effects
    private final int filterId;

    public CameraEffectFx(String name, String iconResourceName, int iconResourceId,
                          int iconResourceIdPressed, int filterId) {
        this.name = name;
        this.iconResourceName = iconResourceName;
        this.iconResourceId = iconResourceId;
        this.iconPressedResourceId = iconResourceIdPressed;
        this.filterId = filterId;
    }

    public static List<CameraEffectFx> getCameraEffectList() {

        List<CameraEffectFx> cameraEffects = new ArrayList<>();

        cameraEffects.add(
                new CameraEffectFx("Fisheye", "FX1", R.drawable.common_filter_fx_fx1_fisheye_normal,
                        R.drawable.common_filter_fx_fx1_fisheye_pressed, Filters.FILTER_FISHEYE));
        cameraEffects.add(
                new CameraEffectFx("Stretch", "FX2",R.drawable.common_filter_fx_fx2_stretch_normal,
                        R.drawable.common_filter_fx_fx2_stretch_pressed, Filters.FILTER_STRETCH));
        cameraEffects.add(
                new CameraEffectFx("Dent", "FX3",R.drawable.common_filter_fx_fx3_dent_normal,
                        R.drawable.common_filter_fx_fx3_dent_pressed, Filters.FILTER_DENT));
        cameraEffects.add(
                new CameraEffectFx("Mirror", "FX4",R.drawable.common_filter_fx_fx4_mirror_normal,
                        R.drawable.common_filter_fx_fx4_mirror_pressed, Filters.FILTER_MIRROR));
        cameraEffects.add(
                new CameraEffectFx("Squeeze", "FX5",R.drawable.common_filter_fx_fx5_squeeze_normal,
                        R.drawable.common_filter_fx_fx5_squeeze_pressed, Filters.FILTER_SQUEEZE));
        cameraEffects.add(
                new CameraEffectFx("Tunnel", "FX6",R.drawable.common_filter_fx_fx6_tunnel_normal,
                        R.drawable.common_filter_fx_fx6_tunnel_pressed, Filters.FILTER_TUNNEL));
        cameraEffects.add(
                new CameraEffectFx("Twirl", "FX7",R.drawable.common_filter_fx_fx7_twirl_normal,
                        R.drawable.common_filter_fx_fx7_twirl_pressed,Filters.FILTER_TWIRL));
        cameraEffects.add(
                new CameraEffectFx("Bulge", "FX8",R.drawable.common_filter_fx_fx8_bulge_normal,
                        R.drawable.common_filter_fx_fx8_bulge_pressed, Filters.FILTER_BULGE));

        return cameraEffects;
    }

    public String getName() {
        return name;
    }

    public String getIconResourceName() { return iconResourceName; }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public int getIconPressedResourceId() {
        return iconPressedResourceId;
    }

    public int getFilterId() {
        return filterId;
    }


}
