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

import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

import java.util.ArrayList;

/**
 * Project representation. Contains reference to all media, audio, transitions and effects used in
 * the video edition. A project can be created, opened, saved, deleted and shared with other users.
 * Every time a user opens a project all previous changes must be accessible to him or her.
 */
public class Project {

    /**
     * There could be just one project open at a time. So this converts Project in a Singleton.
     */
    private static Project INSTANCE;

    /**
     * Project name. Also it will be the name of the exported video
     */
    private String title;
    /**
     * The folder where de temp files of the project are stored
     */
    private String projectPath;
    /**
     * Track of Video an Image objects
     */
    private MediaTrack mediaTrack;
    /**
     * Audio tracks to form the final audio track. One by default, could be maximum defined on
     * project profile.
     */
    private ArrayList<AudioTrack> audioTracks;
    /**
     * Total duration of the video project.
     */
    private long duration;
    /**
     * Project profile. Defines some limitations and characteristic of the project based on user
     * subscription.
     */
    private Profile profile;

    /**
     * Default constructor.
     *
     * @param title - Project and final video name.
     * @param projectPath - Path to root folder for the current project.
     * @param mediaTrack - Video track of the project.
     * @param audioTracks - Audio tracks of the project.
     * @param profile - Define some characteristics and limitations of the current project.
     */
    public Project(String title, String projectPath, MediaTrack mediaTrack,
                   ArrayList<AudioTrack> audioTracks, Profile profile) {
        this.title = title;
        this.projectPath = projectPath;
        this.mediaTrack = mediaTrack;
        this.audioTracks = audioTracks;
        this.profile = profile;
        this.duration = mediaTrack.getDuration();
    }

    /**
     * Project factory.
     *
     * @return - Singleton instance of the current project.
     */
    public Project getInstance(String title, String projectPath, MediaTrack mediaTrack,
                               Profile profile){
        if (INSTANCE==null){
            INSTANCE=new Project(title, projectPath, mediaTrack, audioTracks, profile);
        }
        return INSTANCE;
    }
}