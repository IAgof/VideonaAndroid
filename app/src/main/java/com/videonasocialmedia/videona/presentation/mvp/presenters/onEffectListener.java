/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;

import java.util.ArrayList;

public interface onEffectListener {

    public void onEffectAdded(Effect effect, long time);

    public void onEffectRemoved(Effect effect, long time);

    public void onEffectListRetrieved(ArrayList<String> effect);

}
