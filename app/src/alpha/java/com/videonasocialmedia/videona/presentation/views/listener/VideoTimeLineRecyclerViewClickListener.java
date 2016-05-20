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

    void onClipClicked(int position);

    void onClipLongClicked();

    void onClipRemoveClicked(int position);

    void onClipMoved(int toPosition);
}
