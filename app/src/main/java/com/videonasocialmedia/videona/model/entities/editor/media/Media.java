/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor.media;

import android.media.MediaMetadata;

import com.videonasocialmedia.videona.model.entities.editor.EditorElement;

import java.util.ArrayList;

/**
 * Created by jca on 30/3/15.
 */
public abstract class Media extends EditorElement {
    /**
     * The path of the media resource
     */
    protected String mediaPath;
    /**
     * The long of the media resource to be added to the project
     */
    protected long duration;
    /**
     * The start time of the media resource.
     * E.g. In a five minutes video from which second 30 to 35 is needed. The media object
     * has got a startTime of 3000 ms and a duration of 5000 ms
     */
    protected long startTime;
    /**
     * Title of the media. Should be the video name in the social network
     */
    protected String title;

    /**
     * List with de authors id;
     */
    protected ArrayList<Integer> authors;

    /**
     * Metadata of the audio resource
     */
    MediaMetadata metadata;


    protected Media(String mediaPath, long duration, long startTime, String title, ArrayList<Integer> authors, MediaMetadata metadata) {
        this.mediaPath = mediaPath;
        this.duration = duration;
        this.startTime = startTime;
        this.title = title;
        this.authors = authors;
        this.metadata = metadata;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Integer> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Integer> authors) {
        this.authors = authors;
    }

    /**
     * Add an author to the media
     * @param authorId
     */
    public void addAuthor (Integer authorId){
        this.authors.add(authorId);
    }

    /**
     * Add a list of authors to the media
     * @param authors
     */
    public void addAuthors (ArrayList<Integer> authors){
        this.authors.addAll(authors);
    }

    public MediaMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(MediaMetadata metadata) {
        this.metadata = metadata;
    }
}
