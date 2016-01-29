/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import java.util.ArrayList;

public interface OnColorEffectListener {

    void onColorEffectAdded(String colorEffect, long time);

    void onColorEffectRemoved(String colorEffect, long time);

    void onColorEffectListRetrieved(ArrayList<String> effect);

}
