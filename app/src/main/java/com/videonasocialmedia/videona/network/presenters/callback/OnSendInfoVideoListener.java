/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network.presenters.callback;

/**
 * Created by alvaro on 5/07/16.
 */
public interface OnSendInfoVideoListener {

    void onSendInfoVideoError(Causes causes);

    void onSendInfoSuccess();

    enum Causes {
        NETWORK_ERROR, CREDENTIALS_EXPIRED, UNKNOWN_ERROR, CREDENTIALS_UNKNOWN
    }

}
