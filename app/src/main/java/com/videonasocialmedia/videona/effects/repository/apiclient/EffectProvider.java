/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.apiclient;

import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.auth.domain.model.PermissionType;
import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.EffectType;
import com.videonasocialmedia.videona.effects.domain.model.OverlayEffect;
import com.videonasocialmedia.videona.effects.domain.model.ShaderEffect;
import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.PromoRepository;
import com.videonasocialmedia.videona.promo.repository.local.PromosLocalSource;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class EffectProvider {

    private static boolean DEFAULT_EFFECT_TYPE_ACTIVATED = false;

    public static List<Effect> getShaderEffectList() {

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

        overlayList.add(new OverlayEffect("ovt006", "Autumn",
            R.drawable.common_filter_overlay_login,
            R.drawable.common_filter_overlay_ovt006_autumn,
            R.drawable.overlay_filter_autumn,
            EffectType.OVERLAY.name(),
            PermissionType.LOGGED_IN.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovw001", "Mexico",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovw001_mexico,
            R.drawable.overlay_filter_mexico,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovf005", "CCTV",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovf005_cctv,
            R.drawable.overlay_filter_cctv,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovh003", "Tattoo",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovh003_tattoo,
            R.drawable.overlay_filter_tattoo,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovp004", "Dog",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovp004_dog,
            R.drawable.overlay_filter_dog,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovh005", "Surf",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovh005_surf,
            R.drawable.overlay_filter_surf,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovb004", "Birthday",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovb004_birthday,
            R.drawable.overlay_filter_birthday,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovr004", "Polygon",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovr004_polygon,
            R.drawable.overlay_filter_polygon,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovi004", "Steampunk",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovi004_steampunk,
            R.drawable.overlay_filter_steampunk,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovs003", "Pop",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovc005_pop,
            R.drawable.overlay_filter_pop,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovp003", "Baby",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovp003_baby,
            R.drawable.overlay_filter_baby,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovi003", "Unicorn",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovi003_unicorn,
            R.drawable.overlay_filter_unicorn,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovf004", "News",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovf004_news,
            R.drawable.overlay_filter_news,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovp001", "Kisscam",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovp001_kiss_cam,
            R.drawable.overlay_filter_kiss_cam,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovi001", "Stain",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovi001_stain,
            R.drawable.overlay_filter_stain,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovb001", "Wasted",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovb001_wasted,
            R.drawable.overlay_filter_wasted,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovb002", "Crash",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovb002_crash,
            R.drawable.overlay_filter_crash,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovh001", "Invaders",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovh001_invaders,
            R.drawable.overlay_filter_invaders,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovh002", "Gamer",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovh002_game,
            R.drawable.overlay_filter_gamer,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovf007", "Retro Style",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovf007_panoramic,
            R.drawable.overlay_filter_panoramic,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovr002", "Party",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovr002_party,
            R.drawable.overlay_filter_party,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovc005", "Bokeh",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovc005_bokeh,
            R.drawable.overlay_filter_bokeh,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovs003", "Vintage",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovf001_vintage,
            R.drawable.overlay_filter_vintage,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        overlayList.add(new OverlayEffect("ovn001", "Rain",
            R.drawable.common_filter_overlay_new,
            R.drawable.common_filter_overlay_ovn001_rain,
            R.drawable.overlay_filter_rain,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));

        if (CachedToken.hasToken()) {
            if (isWolderActive()) {
                overlayList.add(new OverlayEffect("OV27", "Wolder",
                    R.drawable.common_filter_overlay_wolder,
                    R.drawable.common_filter_overlay_ov27_wolder,
                    R.drawable.overlay_filter_wolder,
                    EffectType.OVERLAY.name(),
                    DEFAULT_EFFECT_TYPE_ACTIVATED));
            }
        }

        return overlayList;
    }

    private static boolean isWolderActive() {
      final List<Promo> promotions;
      PromoRepository local = new PromosLocalSource();
      promotions = local.getActivePromos();
      return !promotions.isEmpty();
    }

    public static OverlayEffect getPromocodeOverlayEffect(){
        return (new OverlayEffect("OV27", "Wolder",
            R.drawable.common_filter_overlay_wolder,
            R.drawable.common_filter_overlay_ov27_wolder,
            R.drawable.overlay_filter_wolder,
            EffectType.OVERLAY.name(),
            DEFAULT_EFFECT_TYPE_ACTIVATED));
    }


}
