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

public final class Effect {

    private final String name;
    private final String iconResourceName;
    private final int iconResourceId;
    //filterID is tight coupled with the camera framework. It's necessary to rewrite the effects
    private final int filterId;


    /**
     * @param name                  name of the effect
     * @param iconResourceId        normal icon id
     * @param filterId Filter constant of {@link Filters}
     */
    public Effect(String name, String iconResourceName, int iconResourceId, int filterId) {
        this.name = name;
        this.iconResourceName = iconResourceName;
        this.iconResourceId = iconResourceId;
        this.filterId=filterId;
    }

    public static List<Effect> getColorEffectList() {

        List<Effect> colorEffects = new ArrayList<>();

        colorEffects.add(
                new Effect("aqua", "AD1", R.drawable.common_filter_color_ad1_aqua,
                        Filters.FILTER_AQUA));
        colorEffects.add(
                new Effect("posterize_bw",
                        "AD2", R.drawable.common_filter_color_postericebw,
                        Filters.FILTER_POSTERIZE_BW));
        colorEffects.add(
                new Effect("emboss", "AD3", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS));
        colorEffects.add(
                new Effect("mono", "AD4", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO));
        colorEffects.add(
                new Effect("negative", "AD5", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE));
        colorEffects.add(
                new Effect("night", "AD6", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT));
        colorEffects.add(
                new Effect("posterize", "AD7", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE));
        colorEffects.add(
                new Effect("sepia", "AD8", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA));

        return colorEffects;
    }

    public static List<Effect> getDistortionEffectList() {

        List<Effect> distortionEffects = new ArrayList<>();

        distortionEffects.add(
                new Effect("Fisheye", "FX1", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE));
        distortionEffects.add(
                new Effect("Stretch", "FX2",R.drawable.common_filter_distortion_fx2_stretch,
                        Filters.FILTER_STRETCH));
        distortionEffects.add(
                new Effect("Dent", "FX3", R.drawable.common_filter_distortion_fx3_dent,
                        Filters.FILTER_DENT));
        distortionEffects.add(
                new Effect("Mirror", "FX4",R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR));
        distortionEffects.add(
                new Effect("Squeeze", "FX5",R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE));
        distortionEffects.add(
                new Effect("Tunnel", "FX6",R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL));
        distortionEffects.add(
                new Effect("Twirl", "FX7",R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL));
        distortionEffects.add(
                new Effect("Bulge", "FX8",R.drawable.common_filter_distortion_fx8_bulge,
                        Filters.FILTER_BULGE));

        return distortionEffects;
    }

    public String getName() {
        return name;
    }

    public String getIconResourceName() { return iconResourceName; }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public int getFilterId() {
        return filterId;
    }
}
