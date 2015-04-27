package com.videonasocialmedia.videona.presentation.views.listener;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.views.adapter.ColorEffectAdapter;

public interface ColorEffectClickListener {

    void onEffectClicked(ColorEffectAdapter adapter, Effect effect);

}
