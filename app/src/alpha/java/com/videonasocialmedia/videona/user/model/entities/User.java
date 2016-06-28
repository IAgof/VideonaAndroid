/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.user.model.entities;

/**
 * Videona's user representation
 */
public class User {

    private final String name;
    private final int imageResourceId;
    private final String email;
    private int videosRecorded;
    private int videosShared;

    public User(String name, int imageResourceId, String email) {
        this(name, imageResourceId, email, 0, 0);
    }

    public User(String name, int imageResourceId, String email, int videosRecorded, int videosShared) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.email = email;
        this.videosRecorded = videosRecorded;
        this.videosShared = videosShared;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getEmail() {
        return email;
    }

    public int getVideosRecorded() {
        return videosRecorded;
    }

    public void setVideosRecorded(int videosRecorded) {
        this.videosRecorded = videosRecorded;
    }

    public int getVideosShared() {
        return videosShared;
    }

    public void setVideosShared(int videosShared) {
        this.videosShared = videosShared;
    }
}
