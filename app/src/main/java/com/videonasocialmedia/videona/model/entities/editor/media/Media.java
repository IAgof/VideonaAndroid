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
import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;
import java.util.ArrayList;

/**
 * Abstract representation of any media resource that can be used in the project.
 * TODO habrá que echar un vistazo a esto http://developer.android.com/reference/android/media/package-summary.html
 * Created by jca on 30/3/15.
 */
public abstract class Media extends EditorElement {

    /**
     * Title of the media. Should be the video name in the social network
     */
    protected String title;

    /**
     * The path of the media resource
     */
    protected String mediaPath;

    /**
     * The start time of the media resource within the file it represents.
     */
    protected long fileStartTime;

    /**
     * The long of the media resource to be added to the project
     */
    protected long duration;

    /**
     * Transition before the media item.
     */
    protected Transition opening;

    /**
     * Transition after the media item.
     */
    protected Transition ending;

    /**
     * Metadata of the resource
     */
    protected MediaMetadata metadata;

    /**
     * List with de authors id;
     */
    protected ArrayList<User> authors;

    /**
     * List with de authors names. Used when at least one author is not a videona user.
     */
    protected ArrayList<String> authorsNames;

    /**
     * License of the media file.
     */
    protected License license;

    /**
     * Default media constructor.
     *
     * @param title - Media item title.
     * @param mediaPath - Path to media file.
     * @param mediaStartTime - Time in milliseconds within the associated file where this media item
     *                       begins.
     * @param duration - Duration of this media item.
     * @param metadata - Media item metadata.
     * @param authors - List of authors of this media item.
     * @param authorsNames - List of names of authors. Used when authors are not videona users.
     * @param license - Media item license, inherited from media file.
     */
    protected Media(String title, String mediaPath, long mediaStartTime, MediaMetadata metadata,
                    long duration, ArrayList<User> authors, ArrayList<String> authorsNames,
                    License license) {
        this.title = title;
        this.mediaPath = mediaPath;
        this.fileStartTime = mediaStartTime;
        this.duration = duration;
        this.metadata = metadata;
        this.authors = authors;
        this.authorsNames = authorsNames;
        this.license = license;
        this.opening = this.ending = null;
    }

    public boolean hashTransitions(){
        return (this.opening!=null || this.ending!=null);
    }


    //getters & setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMediaPath() {
        return mediaPath;
    }
    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }
    public long getFileStartTime() {
        return fileStartTime;
    }
    public void setFileStartTime(long fileStartTime) {
        this.fileStartTime = fileStartTime;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public MediaMetadata getMetadata() {
        return metadata;
    }
    public void setMetadata(MediaMetadata metadata) {
        this.metadata = metadata;
    }
    public ArrayList<User> getAuthors() {
        return authors;
    }
    public void setAuthors(ArrayList<User> authors) {
        this.authors = authors;
    }
    public ArrayList<String> getAuthorsNames() {
        return authorsNames;
    }
    public void setAuthorsNames(ArrayList<String> authorsNames) {
        this.authorsNames = authorsNames;
    }
    public License getLicense() {
        return license;
    }
    public void setLicense(License license) {
        this.license = license;
    }
    public Transition getOpening() {
        return opening;
    }
    public void setOpening(Transition opening) {
        if(opening.getAfterMediaItem().equals(this))
            this.opening = opening;
    }
    public Transition getEnding() {
        return ending;
    }
    public void setEnding(Transition ending) {
        if(ending.getBeforeMediaItem().equals(this))
            this.ending = ending;
    }
}
