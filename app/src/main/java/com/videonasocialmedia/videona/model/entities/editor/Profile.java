/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.utils.Size;

/**
 * Project profile. Define some characteristics and limitations of the current video editing project
 * based on User subscription and options.
 */
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
    private ProfileType profileType;

    /**
     * Constructor of minimum number of parameters. In this case coincides with parametrized
     * constructor and therefore is the default constructor. It has all possible atributes for the
     * profile object.
     * <p/>
     * There can be only a single instance of a profile, and therefore this constructor can only be
     * accessed through the factory.
     *
     * @param resolution  - Maximum resolution allowed for the profile.
     * @param maxDuration - Maximum video duration allowed for the profile.
     * @param type        - Profile type.
     */
    private Profile(Size resolution, long maxDuration, ProfileType type) {
        this.resolution = resolution;
        this.maxDuration = maxDuration;
        this.profileType = type;
    }

    /**
     * Profile factory.
     *
     * @param profileType
     * @return - profile instance.
     */
    public static Profile getInstance(ProfileType profileType) {
        if (INSTANCE == null) {
            if (profileType == ProfileType.free) {
                INSTANCE = new Profile(new Size(Size.Resolution.hd720), 1000, profileType);
            } else {
                INSTANCE = new Profile(new Size(Size.Resolution.hd1080), -1, profileType);
            }
        }
        return INSTANCE;
    }

    //getter and setter.
    public Size getResolution() {
        return resolution;
    }

    public void setResolution(Size resolution) {
        if (profileType == ProfileType.pro)
            this.resolution = resolution;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        if (profileType == ProfileType.pro)
            this.maxDuration = maxDuration;
    }

    public ProfileType getProfileType() {
        return profileType;
    }
}
