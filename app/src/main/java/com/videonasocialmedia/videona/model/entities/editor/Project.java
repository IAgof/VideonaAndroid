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

import android.util.Log;

import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.media.Image;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project representation that contains reference to media, audio, transitions and effects used in
 * the current edition job.
 * A project can be created, opened, saved, deleted and shared with other users. Every time a user
 * opens a project all previous changes must be accessible to him or her. However there can be only
 * one
 */
public class Project {

    private final String TAG = getClass().getCanonicalName();
    public static String VIDEONA_PATH = "";
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
     * Track of Video and Image objects
     */
    private MediaTrack mediaTrack;
    /**
     * Audio tracks to form the final audio track. One by default, could be maximum defined on
     * project profile.
     */
    private ArrayList<AudioTrack> audioTracks;
    /**
     * Project profile. Defines some limitations and characteristic of the project based on user
     * subscription.
     */
    private Profile profile;
    /**
     * Project duration. The duration of the project in milliseconds.
     */
    private int duration;
    private Music music;

    /**
     * Constructor of minimum number of parameters. This is the Default constructor.
     *
     * @param title    - Project and final video name.
     * @param rootPath - Path to root folder for the current project.
     * @param profile  - Define some characteristics and limitations of the current project.
     */
    private Project(String title, String rootPath, Profile profile) {
        this.title = title;
        this.projectPath = rootPath + "/projects/" + title; //todo probablemente necesitemos un slugify de ese title.
        this.checkPathSetup(rootPath);
        this.mediaTrack = new MediaTrack();
        this.audioTracks = new ArrayList<>();
        audioTracks.add(new AudioTrack());
        this.profile = profile;
        this.duration = 0;

    }

    /**
     * @param rootPath
     */
    private void checkPathSetup(String rootPath) {

        Project.VIDEONA_PATH = rootPath;
        File projectPath = new File(this.projectPath);
        projectPath.mkdirs();

        Audio.AUDIO_PATH = rootPath + "/audios";
        File audioPath = new File(Audio.AUDIO_PATH + "/thumbs");
        audioPath.mkdirs();

        Image.IMAGE_PATH = rootPath + "/images";
        File imagePath = new File(Image.IMAGE_PATH + "thumbs");
        imagePath.mkdirs();

        Video.VIDEO_PATH = rootPath + "/videos";
        File videoPath = new File(Audio.AUDIO_PATH + "/thumbs");
        videoPath.mkdirs();

    }

    /**
     * Project factory.
     *
     * @return - Singleton instance of the current project.
     */
    public static Project getInstance(String title, String rootPath, Profile profile) {
        if (INSTANCE == null) {
            INSTANCE = new Project(title, rootPath, profile);
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

    public int getDuration() {
        duration = 0;
        for (Media video : mediaTrack.getItems()) {
            duration = duration + video.getDuration();
        }
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void clear() {
        INSTANCE = new Project(null, null, null);
    }

    public int numberOfClips() {
        return getMediaTrack().getItems().size();
    }

    public Music getMusic() {
        /**
         * TODO(jliarte): review this method and matching use case
         * @see com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase
         */
        Music result = null;
        try {
            result = (Music) getAudioTracks().get(0).getItems().get(0);
        } catch (Exception e) {
            Logger log = Logger.getLogger(TAG);
            log.log(Level.FINE, "getMusic: exception trying to load project music");
            e.printStackTrace();
        }
        return result;
    }
}