/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.domain.usecase;

import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;

/**
 *
 */
public class LogoutUser {

    public void logout() {
        CachedToken.deleteToken();
    }

}
