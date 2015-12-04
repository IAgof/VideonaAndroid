package com.videonasocialmedia.videona.domain.effects;

import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;
import com.videonasocialmedia.videona.model.entities.sources.EffectProvider;

import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<ShaderEffect> getColorEffectList() {
        return EffectProvider.getColorEffectList();
    }

    public static List<ShaderEffect> getDistortionEffectList() {
        return EffectProvider.getDistortionEffectList();
    }

}
