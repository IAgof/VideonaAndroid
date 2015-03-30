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

public class Project {

    private static Project INSTANCE;

    /**
     * The name of the project and the name of the exported video
     */
    private String title;
    /**
     * The folder where de temp files of the project are stored
     */
    private String projectPath;
    /**
     * track of Video an Image objects
     */
    private MediaTrack mediaTrack;

    /**
     * the actual length of the project in milliseconds
     */
    private long duration;

    /**
     * The profile of the project
     */
    private Profile profile;

    private Project(String title, String projectPath, MediaTrack mediaTrack, Profile profile) {
        this.title = title;
        this.projectPath = projectPath;
        this.mediaTrack = mediaTrack;
        this.profile = profile;
        duration= mediaTrack.getDuration();
    }

    public Project getInstance(String title, String projectPath, MediaTrack mediaTrack, Profile profile){
        if (INSTANCE==null){
            INSTANCE=new Project(title,projectPath,mediaTrack,profile);
        }
return INSTANCE;
    }
}
