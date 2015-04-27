/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnMediaFinishedListener;

import java.util.ArrayList;

/**
 * Created by vlf on 27/04/2015.
 */
public interface AddMediaUseCase {
    public void loadSelectedMediaItems(ArrayList<String> list, OnMediaFinishedListener listener);
}
