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
 * Project representation that contains reference to media, audio, transitions and effects used in
 * the current edition job.
 * A project can be created, opened, saved, deleted and shared with other users. Every time a user
 * opens a project all previous changes must be accessible to him or her. However there can be only
 * one
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
     * TODO dfa - aquí la duración la veo innecesaria. Si es por rpeguntar por la máxima se le pregunta al profile. Y la actual al track.
     */
    private long duration;

    /**
     * Project profile. Defines some limitations and characteristic of the project based on user
     * subscription.
     */
    private Profile profile;

    /**
     * Parametrized constructor. It has all possible attributes for a Project object.
     *
     * @param title -  the title of the current project.
     * @param projectPath - Project root location.
     * @param mediaTrack - MediaTrack with all media elements used in the current project.
     * @param audioTracks - A list of up to 3 audio tracks for the current project.
     * @param duration - //TODO deprectaed, no tiene sentido usarlo aquí.
     * @param profile - Project profile. Set the default limits by category Profile
     *                //TODO si tenemos una fabrica mejor dejar un solo constructor y privado.
     */
    public Project(String title, String projectPath, MediaTrack mediaTrack,
                   ArrayList<AudioTrack> audioTracks, long duration, Profile profile) {
        this.title = title;
        this.projectPath = projectPath;
        this.mediaTrack = mediaTrack;
        this.audioTracks = audioTracks;
        this.duration = duration;
        this.profile = profile;
    }

    /**
     * Constructor of minimum number of parameters. This is the Default constructor.
     *
     * @param title - Project and final video name.
     * @param projectPath - Path to root folder for the current project.
     * @param profile - Define some characteristics and limitations of the current project.
     */
    private Project(String title, String projectPath, Profile profile) {
        this.title = title;
        this.projectPath = projectPath;
        this.mediaTrack = new MediaTrack();
        this.audioTracks = new ArrayList<AudioTrack>();
        this.profile = profile;
        this.duration = mediaTrack.getDuration();
    }

    /**
     * Project factory.
     *
     * @return - Singleton instance of the current project.
     */
    public static Project getInstance(String title, String projectPath, Profile profile){
        if (INSTANCE==null){
            INSTANCE=new Project(title, projectPath, profile);
        }
        return INSTANCE;
    }

    // getters & setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getProjectPath() {
        return projectPath;
    }
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
    public MediaTrack getMediaTrack() {
        return mediaTrack;
    }
    public void setMediaTrack(MediaTrack mediaTrack) {
        this.mediaTrack = mediaTrack;
    }
    public ArrayList<AudioTrack> getAudioTracks() {
        return audioTracks;
    }
    public void setAudioTracks(ArrayList<AudioTrack> audioTracks) {
        this.audioTracks = audioTracks;
    }
    public Profile getProfile() {
        return profile;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}