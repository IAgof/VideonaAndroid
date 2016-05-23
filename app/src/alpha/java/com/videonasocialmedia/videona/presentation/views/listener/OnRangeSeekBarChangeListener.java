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

import android.view.View;

/**
 * Interface to track the change in range of seekbar.
 */
public interface OnRangeSeekBarChangeListener {
    void setRangeChangeListener(View view, double minPosition, double maxPosition);
}
