package com.videonasocialmedia.videona.model.entities.sources;

import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.effects.OverlayEffect;
import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class EffectProvider {

    public static List<Effect> getColorEffectList() {

        List<Effect> colorEffects = new ArrayList<>();

        colorEffects.add(
                new ShaderEffect("AD1", "aqua", R.drawable.common_filter_color_ad1_aqua,
                        Filters.FILTER_AQUA));
        colorEffects.add(
                new ShaderEffect("AD2", "posterize_bw", R.drawable.common_filter_color_ad2_posterizebw,
                        Filters.FILTER_POSTERIZE_BW));
        colorEffects.add(
                new ShaderEffect("AD3", "emboss", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS));
        colorEffects.add(
                new ShaderEffect("AD4", "mono", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO));
        colorEffects.add(
                new ShaderEffect("AD5", "negative", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE));
        colorEffects.add(
                new ShaderEffect("AD6", "night", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT));
        colorEffects.add(
                new ShaderEffect("AD7", "posterize", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE));
        colorEffects.add(
                new ShaderEffect("AD8", "sepia", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA));
        return colorEffects;
    }

    public static List<Effect> getDistortionEffectList() {

        List<Effect> distortionEffects = new ArrayList<>();

        distortionEffects.add(
                new ShaderEffect("FX1", "Fisheye", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE));
        distortionEffects.add(
                new ShaderEffect("FX2", "Stretch", R.drawable.common_filter_distortion_fx2_stretch,
                        Filters.FILTER_STRETCH));
        distortionEffects.add(
                new ShaderEffect("FX3", "Dent", R.drawable.common_filter_distortion_fx3_dent,
                        Filters.FILTER_DENT));
        distortionEffects.add(
                new ShaderEffect("FX4", "Mirror", R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR));
        distortionEffects.add(
                new ShaderEffect("FX5", "Squeeze", R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE));
        distortionEffects.add(
                new ShaderEffect("FX6", "Tunnel", R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL));
        distortionEffects.add(
                new ShaderEffect("FX7", "Twirl", R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL));
        distortionEffects.add(
                new ShaderEffect("FX8", "Bulge", R.drawable.common_filter_distortion_fx8_bulge,
                        Filters.FILTER_BULGE));

        return distortionEffects;
    }

    public static List<Effect> getOverlayFilterList() {
        List<Effect> overlayList = new ArrayList<>();
        overlayList.add(new OverlayEffect("AUTUMN", "AUTUMN",
                R.drawable.common_filter_overlay_autumn,
                R.drawable.overlay_filter_a));
        overlayList.add(new OverlayEffect("BURN", "BURN",
                R.drawable.common_filter_overlay_burn,
                R.drawable.overlay_filter_b));
        overlayList.add(new OverlayEffect("DARK", "DARK",
                R.drawable.common_filter_overlay_dark,
                R.drawable.overlay_filter_c));
        overlayList.add(new OverlayEffect("INSTANT", "INSTANT",
                R.drawable.common_filter_overlay_instant,
                R.drawable.overlay_filter_d));
        overlayList.add(new OverlayEffect("SUMMER", "SUMMER",
                R.drawable.common_filter_overlay_summer,
                R.drawable.overlay_filter_e));
        overlayList.add(new OverlayEffect("SUNSET", "SUNSET",
                R.drawable.common_filter_overlay_sunset,
                R.drawable.overlay_filter_f));
        overlayList.add(new OverlayEffect("WINTER", "WINTER",
                R.drawable.common_filter_overlay_winter,
                R.drawable.overlay_filter_g));
        overlayList.add(new OverlayEffect("WASTED", "WASTED",
                R.drawable.common_filter_overlay_wasted,
                R.drawable.overlay_filter_h));
        return overlayList;
    }

}