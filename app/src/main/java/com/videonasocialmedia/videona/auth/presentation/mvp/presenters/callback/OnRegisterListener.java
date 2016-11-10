/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback;

/**
 *
 */
public interface OnRegisterListener {
    void onRegisterError(Causes causes);

    void onRegisterSuccess();

    enum Causes {
        NETWORK_ERROR, UNKNOWN_ERROR, USER_ALREADY_EXISTS, INVALID_EMAIL, MISSING_REQUEST_PARAMETERS, INVALID_PASSWORD,
    }

}
