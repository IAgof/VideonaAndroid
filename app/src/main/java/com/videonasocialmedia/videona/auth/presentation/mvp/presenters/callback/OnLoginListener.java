/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback;

/**
 * Created by alvaro on 15/06/16.
 */
public interface OnLoginListener {

    void onLoginError(Causes causes);

    void onLoginSuccess();

    enum Causes {
        NETWORK_ERROR, CREDENTIALS_EXPIRED, UNKNOWN_ERROR, CREDENTIALS_UNKNOWN
    }


}
