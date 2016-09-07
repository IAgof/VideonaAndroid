package com.videonasocialmedia.videona.model.sources;

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
                        Filters.FILTER_AQUA, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD2", "Posterizebw", R.drawable.common_filter_color_ad2_posterizebw,
                        Filters.FILTER_POSTERIZE_BW, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD3", "Emboss", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD4", "Mono", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD5", "Negative", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD6", "Night", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD7", "Posterize", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE, AnalyticsConstants.FILTER_TYPE_COLOR));
        colorEffects.add(
                new ShaderEffect("AD8", "Sepia", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA, AnalyticsConstants.FILTER_TYPE_COLOR));
        return colorEffects;
    }

    public static List<Effect> getDistortionEffectList() {

        List<Effect> distortionEffects = new ArrayList<>();

        distortionEffects.add(
                new ShaderEffect("FX1", "Fisheye", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE, AnalyticsConstants.FILTER_TYPE_DISTORTION));

        distortionEffects.add(
                new ShaderEffect("FX4", "Mirror", R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX5", "Squeeze", R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX6", "Tunnel", R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        distortionEffects.add(
                new ShaderEffect("FX7", "Twirl", R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL, AnalyticsConstants.FILTER_TYPE_DISTORTION));


        return distortionEffects;
    }

    public static List<Effect> getShaderEffectList(){

        List<Effect> shaderEffects = new ArrayList<>();

        shaderEffects.add(
                new ShaderEffect("FX4", "Mirror", R.drawable.common_filter_distortion_fx4_mirror,
                        Filters.FILTER_MIRROR, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX5", "Squeeze", R.drawable.common_filter_distortion_fx5_squeeze,
                        Filters.FILTER_SQUEEZE, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("AD7", "Posterize", R.drawable.common_filter_color_ad7_posterize,
                        Filters.FILTER_POSTERIZE, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("FX1", "Fisheye", R.drawable.common_filter_distortion_fx1_fisheye,
                        Filters.FILTER_FISHEYE, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX7", "Twirl", R.drawable.common_filter_distortion_fx7_twirl,
                        Filters.FILTER_TWIRL, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX2", "Stretch", R.drawable.common_filter_distortion_fx2_stretch,
                        Filters.FILTER_STRETCH, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("FX6", "Tunnel", R.drawable.common_filter_distortion_fx6_tunnel,
                        Filters.FILTER_TUNNEL, AnalyticsConstants.FILTER_TYPE_DISTORTION));
        shaderEffects.add(
                new ShaderEffect("AD5", "Negative", R.drawable.common_filter_color_ad5_negative,
                        Filters.FILTER_NEGATIVE, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD4", "Mono", R.drawable.common_filter_color_ad4_mono,
                        Filters.FILTER_MONO, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD1", "Aqua", R.drawable.common_filter_color_ad1_aqua,
                        Filters.FILTER_AQUA, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD2", "Posterizebw", R.drawable.common_filter_color_ad2_posterizebw,
                        Filters.FILTER_POSTERIZE_BW, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD3", "Emboss", R.drawable.common_filter_color_ad3_emboss,
                        Filters.FILTER_EMBOSS, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD8", "Sepia", R.drawable.common_filter_color_ad8_sepia,
                        Filters.FILTER_SEPIA, AnalyticsConstants.FILTER_TYPE_COLOR));
        shaderEffects.add(
                new ShaderEffect("AD6", "Green", R.drawable.common_filter_color_ad6_green,
                        Filters.FILTER_NIGHT, AnalyticsConstants.FILTER_TYPE_COLOR));

        return shaderEffects;
    }

    public static List<Effect> getOverlayFilterList() {

        List<Effect> overlayList = new ArrayList<>();
        overlayList.add(new OverlayEffect("GIFT_OVg01", " ",
                R.drawable.common_filter_overlay_gift,
                R.drawable.overlay_filter_pop, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovw001", "Mexico",
                R.drawable.common_filter_overlay_ovw001_mexico,
                R.drawable.overlay_filter_mexico, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovf005", "CCTV",
                R.drawable.common_filter_overlay_ovf005_cctv,
                R.drawable.overlay_filter_cctv, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovh003", "Tattoo",
                R.drawable.common_filter_overlay_ovh003_tattoo,
                R.drawable.overlay_filter_tattoo, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovp004", "Dog",
                R.drawable.common_filter_overlay_ovp004_dog,
                R.drawable.overlay_filter_dog, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovh005", "Surf",
                R.drawable.common_filter_overlay_ovh005_surf,
                R.drawable.overlay_filter_surf, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovb004", "Birthday",
                R.drawable.common_filter_overlay_ovb004_birthday,
                R.drawable.overlay_filter_birthday, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovr004", "Polygon",
                R.drawable.common_filter_overlay_ovr004_polygon,
                R.drawable.overlay_filter_polygon, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovi004", "Steampunk",
                R.drawable.common_filter_overlay_ovi004_steampunk,
                R.drawable.overlay_filter_steampunk, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovs003", "Holidays",
                R.drawable.common_filter_overlay_ovs003_holidays,
                R.drawable.overlay_filter_holidays, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovp003", "Baby",
                R.drawable.common_filter_overlay_ovp003_baby,
                R.drawable.overlay_filter_baby, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovi003", "Unicorn",
                R.drawable.common_filter_overlay_ovi003_unicorn,
                R.drawable.overlay_filter_unicorn, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovf004", "News",
                R.drawable.common_filter_overlay_ovf004_news,
                R.drawable.overlay_filter_news, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovp001", "Kisscam",
                R.drawable.common_filter_overlay_ovp001_kiss_cam,
                R.drawable.overlay_filter_kiss_cam, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovi001", "Stain",
                R.drawable.common_filter_overlay_ovi001_stain,
                R.drawable.overlay_filter_stain, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovb001", "Wasted",
                R.drawable.common_filter_overlay_ovb001_wasted,
                R.drawable.overlay_filter_wasted, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovb002", "Crash",
                R.drawable.common_filter_overlay_ovb002_crash,
                R.drawable.overlay_filter_crash, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovh001", "Invaders",
                R.drawable.common_filter_overlay_ovh001_invaders,
                R.drawable.overlay_filter_invaders, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovh002", "Gamer",
                R.drawable.common_filter_overlay_ovh002_game,
                R.drawable.overlay_filter_gamer, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovs002", "Dive",
                R.drawable.common_filter_overlay_ovs002_dive,
                R.drawable.overlay_filter_diving, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovr002", "Party",
                R.drawable.common_filter_overlay_ovr002_party,
                R.drawable.overlay_filter_party, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovc005", "Bokeh",
                R.drawable.common_filter_overlay_ovc005_bokeh,
                R.drawable.overlay_filter_bokeh, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovs003", "Vintage",
                R.drawable.common_filter_overlay_ovf001_vintage,
                R.drawable.overlay_filter_vintage, AnalyticsConstants.FILTER_TYPE_OVERLAY));

        overlayList.add(new OverlayEffect("ovn001", "Rain",
                R.drawable.common_filter_overlay_ovn001_rain,
                R.drawable.overlay_filter_rain, AnalyticsConstants.FILTER_TYPE_OVERLAY));


        return overlayList;
    }

    public static Effect getOverlayEffectGift() {
        return new OverlayEffect("ovc005", "Pop",
                R.drawable.common_filter_overlay_ovc005_pop,
                R.drawable.overlay_filter_pop, AnalyticsConstants.FILTER_TYPE_OVERLAY);
    }

}
