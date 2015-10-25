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

public final class CameraEffectColor {

    private final String name;
    private final int iconResourceId;
    private final int iconPressedResourceId;
    //filterID is tight coupled with the camera framework. It's necessary to rewrite the effects
    private final int filterId;


    /**
     * @param name                  name of the effect
     * @param iconResourceId        normal icon id
     * @param iconResourceIdPressed pressed icon id
     * @param filterId Filter constant of {@link com.videonasocialmedia.avrecorder.Filters}
     */
    public CameraEffectColor(String name, int iconResourceId, int iconResourceIdPressed,
                             int filterId) {
        this.name = name;
        this.iconResourceId = iconResourceId;
        this.iconPressedResourceId = iconResourceIdPressed;
        this.filterId=filterId;
    }

    public static List<CameraEffectColor> getDefaultCameraEffectColorList() {

        List<CameraEffectColor> cameraEffectColor = new ArrayList<>();

        cameraEffectColor.add(
                new CameraEffectColor("aqua", R.drawable.common_filter_ad1_aqua_normal,
                        R.drawable.common_filter_ad1_aqua_pressed, Filters.FILTER_AQUA));
        cameraEffectColor.add(
                new CameraEffectColor("posterize_bw",
                        R.drawable.common_filter_ad2_postericebw_normal,
                        R.drawable.common_filter_ad2_postericebw_pressed,
                        Filters.FILTER_POSTERIZE_BW));
        cameraEffectColor.add(
                new CameraEffectColor("emboss", R.drawable.common_filter_ad3_emboss_normal,
                        R.drawable.common_filter_ad3_emboss_pressed, Filters.FILTER_EMBOSS));
        cameraEffectColor.add(
                new CameraEffectColor("mono", R.drawable.common_filter_ad4_mono_normal,
                        R.drawable.common_filter_ad4_mono_pressed, Filters.FILTER_MONO));
        cameraEffectColor.add(
                new CameraEffectColor("negative", R.drawable.common_filter_ad5_negative_normal,
                        R.drawable.common_filter_ad5_negative_pressed, Filters.FILTER_NEGATIVE));
        cameraEffectColor.add(
                new CameraEffectColor("night", R.drawable.common_filter_ad6_green_normal,
                        R.drawable.common_filter_ad6_green_pressed, Filters.FILTER_NIGHT));
        cameraEffectColor.add(
                new CameraEffectColor("posterize", R.drawable.common_filter_ad7_posterize_normal,
                        R.drawable.common_filter_ad7_posterize_pressed, Filters.FILTER_POSTERIZE));
        cameraEffectColor.add(
                new CameraEffectColor("sepia", R.drawable.common_filter_ad8_sepia_normal,
                        R.drawable.common_filter_ad8_sepia_pressed, Filters.FILTER_SEPIA));

        return cameraEffectColor;
    }

    public String getName() {
        return name;
    }

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
