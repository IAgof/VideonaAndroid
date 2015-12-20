package com.videonasocialmedia.videona.domain.effects;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.effects.ShaderEffect;
import com.videonasocialmedia.videona.model.entities.sources.EffectProvider;

import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<Effect> getColorEffectList() {
        return EffectProvider.getColorEffectList();
    }

    public static List<Effect> getDistortionEffectList() {
        return EffectProvider.getDistortionEffectList();
    }

    public static List<Effect> getOverlayEffectsList() {
        return EffectProvider.getOverlayFilterList();
    }
}
