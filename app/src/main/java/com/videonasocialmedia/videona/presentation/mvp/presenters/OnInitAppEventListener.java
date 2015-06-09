/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

public interface OnInitAppEventListener {

    public void onCheckPathsAppSuccess();

    public void onCheckPathsAppError();

    public void onLoadingProjectSuccess();

    public void onLoadingProjectError();
}
