/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.listener;

/**
 * Created by jca on 25/3/15.
 */
public interface VideoTimeLineRecyclerViewClickListener {

    void onVideoClicked(int position);

    void onVideoLongClicked();

    void onVideoRemoveClicked(int position);
}
