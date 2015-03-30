/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.model.entities.editor;


public class Profile {

    private static Profile INSTANCE;

    /**
     * possible profileTypes
     */
    public static enum ProfileType {
        free, pro
    }

    /**
     * Resolution of the Video objects in a project
     */
    private Size resolution;

    /**
     * Maximum length of the project in millseconds;
     * if the value is negative the project duration has no limitation
     */
    private long maxDuration;

    /**
     * type of profile
     */
    private  ProfileType profileType;

    //TODO

    private Profile(Size resolution, long maxDuration, ProfileType type) {
        this.resolution = resolution;
        this.maxDuration = maxDuration;
        this.profileType= type;

    }

    public static Profile getInstance(ProfileType profileType) {
        if (INSTANCE == null) {
            if (profileType == ProfileType.free) {
                //instantiate the free profile
                INSTANCE = new Profile(new Size(Size.Resolution.hd720), 1000, profileType);
            } else {
                INSTANCE = new Profile(new Size(Size.Resolution.hd1080), -1, profileType);
            }
        }
        return INSTANCE;
    }

    public Size getResolution() {
        return resolution;
    }

    /**
     * If the profile is "pro" sets a new resolution value. Does nothing otherwise
     * @param resolution the new resolution
     */
    public void setResolution(Size resolution) {
        if (profileType==ProfileType.pro) {
            this.resolution = resolution;
        }
    }

    public long getMaxDuration() {
        return maxDuration;
    }

}
