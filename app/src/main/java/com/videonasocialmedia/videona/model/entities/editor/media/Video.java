/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.videona.model.entities.editor.media;

import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

import java.util.ArrayList;

/**
 * A media video item that represents a file (or part of a file) that can be used in project video
 * track.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
 */
public class Video extends Media {

    public static String VIDEO_PATH;

    /**
     * The total duration of the file media resource
     */
    private int fileDuration;
    /**
     * Define if a video has been splitted before
     */
    private boolean isSplit;

    // TODO(jliarte): 14/06/16 this entity should not depend on MediaMetadataRetriever as it is part of android
    /* Needed to allow mockito inject it */
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();


    /**
     * protected default empty constructor, trying to get injectMocks working
     */
    protected Video() {
        super();
    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String mediaPath, int fileStartTime,
                 int duration, ArrayList<User> authors, License license) {
        super(identifier, iconPath, mediaPath, fileStartTime, duration, authors, license);
        fileDuration = getFileDuration(mediaPath);
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String identifier, String iconPath, String selectedIconPath, String title,
                 String mediaPath, int fileStartTime, int duration, Transition opening,
                 Transition ending, MediaMetadata metadata, ArrayList<User> authors,
                 License license) {
        super(identifier, iconPath, selectedIconPath, title, mediaPath, fileStartTime, duration,
                opening, ending, metadata, authors, license);
        fileDuration = getFileDuration(mediaPath);
    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.media.Media
     */
    public Video(String mediaPath) {
        super(null, null, mediaPath, 0, 0, null, null);
        try {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mediaPath);
            duration = Integer.parseInt(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION));
            fileDuration = duration;
            startTime = 0;
            stopTime = duration;
            isSplit = false;
        } catch (Exception e) {
            fileDuration = 0;
            duration = 0;
            stopTime = 0;
            isSplit = false;
        }
    }

    public Video(String mediaPath, int fileStartTime, int duration) {
        super(null, null, mediaPath, fileStartTime, duration, null, null);
        fileDuration = getFileDuration(mediaPath);
    }

    public Video(Video video) {
        super(null, null, video.getMediaPath(), video.getStartTime(),
                video.getDuration(), null, null);
        fileDuration = getFileDuration(video.getMediaPath());
        stopTime = video.getStopTime();
    }

    public int getFileDuration() {
        return fileDuration;
    }

    public boolean getIsSplit() {
        return isSplit;
    }

    public void setIsSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }

    private int getFileDuration(String path){
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return Integer.parseInt(retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

}
