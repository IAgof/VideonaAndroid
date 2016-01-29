package com.videonasocialmedia.videona.model.entities.sources;

import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.effects.OverlayEffect;
import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class EffectProvider {

    public static List<Effect> getColorEffectList() {

        List<Effect> colorEffects = new ArrayList<>();

        colorEffects.add(
                new ShaderEffect("AD1", "Aqua", R.drawable.common_filter_color_ad1_aqua,
                        Filters.FILTER_AQUA, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD2", "Posterizebw", R.drawable.common_filter_color_ad2_posterizebw,
                        Filters.FILTER_POSTERIZE_BW, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD3", "Emboss", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD4", "Mono", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD5", "Negative", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD6", "Night", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD7", "Posterize", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE, AnalyticsConstants.TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD8", "Sepia", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA, AnalyticsConstants.TYPE_COLOR));
        return colorEffects;
    }

    public static List<Effect> getDistortionEffectList() {

        List<Effect> distortionEffects = new ArrayList<>();

        distortionEffects.add(
                new ShaderEffect("FX1", "Fisheye", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX2", "Stretch", R.drawable.common_filter_distortion_fx2_stretch,
                        Filters.FILTER_STRETCH, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX3", "Dent", R.drawable.common_filter_distortion_fx3_dent,
                        Filters.FILTER_DENT, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX4", "Mirror", R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX5", "Squeeze", R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX6", "Tunnel", R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX7", "Twirl", R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL, AnalyticsConstants.TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX8", "Bulge", R.drawable.common_filter_distortion_fx8_bulge,
                        Filters.FILTER_BULGE, AnalyticsConstants.TYPE_DISTORTION));

        return distortionEffects;
    }

    public static List<Effect> getShaderEffectList(){

        List<Effect> shaderEffects = new ArrayList<>();

        shaderEffects.add(
                new ShaderEffect("FX1", "Fisheye", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX2", "Stretch", R.drawable.common_filter_distortion_fx2_stretch,
                        Filters.FILTER_STRETCH, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX3", "Dent", R.drawable.common_filter_distortion_fx3_dent,
                        Filters.FILTER_DENT, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX4", "Mirror", R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX5", "Squeeze", R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX6", "Tunnel", R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX7", "Twirl", R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX8", "Bulge", R.drawable.common_filter_distortion_fx8_bulge,
                        Filters.FILTER_BULGE, AnalyticsConstants.TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("AD1", "Aqua", R.drawable.common_filter_color_ad1_aqua,
                        Filters.FILTER_AQUA, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD2", "Posterizebw", R.drawable.common_filter_color_ad2_posterizebw,
                        Filters.FILTER_POSTERIZE_BW, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD3", "Emboss", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD4", "Mono", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD5", "Negative", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD6", "Night", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD7", "Posterize", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE, AnalyticsConstants.TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD8", "Sepia", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA, AnalyticsConstants.TYPE_COLOR));

        return shaderEffects;
    }

    public static List<Effect> getOverlayFilterList() {
        List<Effect> overlayList = new ArrayList<>();
        overlayList.add(new OverlayEffect("OV1", "Burn",
                R.drawable.common_filter_overlay_ov1_burn,
                R.drawable.overlay_filter_burn, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV3", "Sunset",
                R.drawable.common_filter_overlay_ov3_sunset,
                R.drawable.overlay_filter_sunset, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV4", "Retrotv",
                R.drawable.common_filter_overlay_ov4_retrotv,
                R.drawable.overlay_filter_retrotv, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV5", "Autumn",
                R.drawable.common_filter_overlay_ov5_autumn,
                R.drawable.overlay_filter_autumn, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV6", "Mist",
                R.drawable.common_filter_overlay_ov6_mist,
                R.drawable.overlay_filter_mist, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV7", "Pride",
                R.drawable.common_filter_overlay_ov7_pride,
                R.drawable.overlay_filter_pride, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV9", "Summer",
                R.drawable.common_filter_overlay_ov9_summer,
                R.drawable.overlay_filter_summer, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV10", "CCTV",
                R.drawable.common_filter_overlay_ov10_cctv,
                R.drawable.overlay_filter_cctv, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV13", "Passion",
                R.drawable.common_filter_overlay_ov13_passion,
                R.drawable.overlay_filter_passion, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV14", "Stain",
                R.drawable.common_filter_overlay_ov14_stain,
                R.drawable.overlay_filter_color_stain, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV15", "Pastel",
                R.drawable.common_filter_overlay_ov15_pastel,
                R.drawable.overlay_filter_pastel, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV16", "Game",
                R.drawable.common_filter_overlay_ov16_game,
                R.drawable.overlay_filter_game, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV17", "Wasted",
                R.drawable.common_filter_overlay_ov17_wasted,
                R.drawable.overlay_filter_wasted, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV18", "Polaroid",
                R.drawable.common_filter_overlay_ov18_polaroid,
                R.drawable.overlay_filter_polaroid, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV19", "Old",
                R.drawable.common_filter_overlay_ov19_old,
                R.drawable.overlay_filter_old, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV22", "Rain",
                R.drawable.common_filter_overlay_ov22_rain,
                R.drawable.overlay_filter_rain, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV23", "Dark",
                R.drawable.common_filter_overlay_ov23_dark,
                R.drawable.overlay_filter_dark, AnalyticsConstants.TYPE_OVERLAY));
        overlayList.add(new OverlayEffect("OV24", "Bokeh",
                R.drawable.common_filter_overlay_ov24_bokeh,
                R.drawable.overlay_filter_bokeh, AnalyticsConstants.TYPE_OVERLAY));

        return overlayList;
    }

}
