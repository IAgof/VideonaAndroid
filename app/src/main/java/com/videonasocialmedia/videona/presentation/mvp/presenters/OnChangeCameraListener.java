/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

public interface OnChangeCameraListener {

    void onChangeCameraSuccess( boolean isFlashSupported, boolean isAutoFocusSupported);

    void onChangeCameraError();


}
