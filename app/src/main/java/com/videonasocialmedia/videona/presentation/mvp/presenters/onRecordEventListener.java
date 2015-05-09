/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

public interface onRecordEventListener {

    public void onRecordStarted();

    public void onRecordStopped();

    public void onRecordRestarted();

}
