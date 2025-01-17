/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

public interface JoinBetaView {
    void showMessage(int messageId);
    void hideDialog();
    void setEmail(String email);
    void goToBeta();
}
