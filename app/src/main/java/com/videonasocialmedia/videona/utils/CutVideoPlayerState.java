/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 */

package com.videonasocialmedia.videona.utils;

import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;

public class CutVideoPlayerState {

    //private String filename = EditActivity.videoRecorded;
    private int start = 0, stop = 0;
    private int currentTime = 0;
    private String messageText;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

//    public String getFilename() {
//        return filename;
//    }

//    public void setFilename(String filename) {
//        this.filename = filename;
//    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public void reset() {
        start = stop = 0;
    }

    public int getDuration() {

        return stop - start;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isValid() {
        return stop > start;
    }
}
