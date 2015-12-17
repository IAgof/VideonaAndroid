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

package com.videonasocialmedia.videona.presentation.mvp.views;


import com.videonasocialmedia.videona.presentation.mvp.views.RecordBaseView;
import com.videonasocialmedia.videona.presentation.views.adapter.Effect;

import java.util.List;

public interface RecordView extends RecordBaseView {

    void showMenuOptions();

    void hideMenuOptions();

    void lockScreenRotation();

    void unlockScreenRotation();

}
