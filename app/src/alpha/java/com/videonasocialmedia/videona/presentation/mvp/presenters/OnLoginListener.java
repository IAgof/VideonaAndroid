/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

/**
 * Created by alvaro on 15/06/16.
 */
public interface OnLoginListener {

    void onLoginError(int stringError);

    void onLoginSuccess();
}
