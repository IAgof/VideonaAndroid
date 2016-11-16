/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.listener;


import com.videonasocialmedia.videona.effects.domain.model.Effect;

/**
 * Created by Veronica Lago Fominaya on 03/11/2015.
 */
public interface OnEffectSelectedListener {
    /**
     * @param effect
     */
    void onEffectSelected(Effect effect);

    void onEffectSelectionCancel(Effect effect);
}
