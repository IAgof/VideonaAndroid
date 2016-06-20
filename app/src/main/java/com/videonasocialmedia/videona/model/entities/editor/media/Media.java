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

import java.io.File;
import java.util.ArrayList;

/**
 * Abstract representation of any media resource that can be used in the project.
 * TODO habrá que echar un vistazo a esto http://developer.android.com/reference/android/media/package-summary.html
 *
 * @author Juan Javier Cabanas
 * @author Álvaro Martínez Marco
 * @author Danny R. Fonseca Arboleda
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

    // TODO(jliarte): 14/06/16 seems to not being used. If so, maybe initialize in getter?
//    protected File source;

    /**
     * The start time of the media resource within the file it represents.
     */
    protected int fileStartTime;

    /**
     * The stop time of the media resource within the file it represents.
     */
    protected int fileStopTime;

    /**
     * @deprecated
     * It's not necessary to set this param. In getter calculates the difference between stop time
     * and start time
     *
     * The long of the media resource to be added to the project
     */
    protected int duration;

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
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @param identifier    - Unique identifier of the media for the current project.
     * @param iconPath      - Path to a resource that allows represent the media in the view.
     * @param mediaPath     - Path to the resource file that this media represents.
     * @param fileStartTime - Media item initial time in milliseconds within the file referenced
     * @param duration      - Media item duration in milliseconds within the file referenced
     * @param license       - Legal stuff.
     * @param authors       - List of authors of the media item.
     */
    protected Media(String identifier, String iconPath, String mediaPath, int fileStartTime,
                    int duration, ArrayList<User> authors, License license) {
        super(identifier, iconPath);
        this.mediaPath = mediaPath;
//        this.source = new File(this.mediaPath);
        this.fileStartTime = fileStartTime;
        this.duration = duration;
        this.fileStopTime = duration;
        this.authors = authors;
        this.license = license;
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @param identifier       - Unique identifier of the media for the current project.
     * @param iconPath         - Path to a resource that allows represent the media in the view.
     * @param selectedIconPath - if not null used as icon when something interact with the element.
     *                         If null it will be used the iconPath as default.
     * @param title            - Human friendly title for the media item.
     * @param mediaPath        - Path to the resource file that this media represents.
     * @param fileStartTime    - Media item initial time in milliseconds within the file referenced
     * @param duration         - Media item duration in milliseconds within the file referenced
     * @param opening          - reference to a transition after the media item in the track.
     * @param ending           - reference to a transition before the media item in the track.
     * @param metadata         - File metadata.
     * @param license          - Legal stuff.
     * @param authors          - List of authors of the media item.
     */
    protected Media(String identifier, String iconPath, String selectedIconPath, String title,
                    String mediaPath, int fileStartTime, int duration, Transition opening,
                    Transition ending, MediaMetadata metadata, ArrayList<User> authors,
                    License license) {
        super(identifier, iconPath, selectedIconPath);
        this.title = title;
        this.mediaPath = mediaPath;
//        this.source = new File(this.mediaPath);
        this.fileStartTime = fileStartTime;
        this.fileStopTime = duration;
        this.duration = duration;
        this.opening = opening;
        this.ending = ending;
        this.metadata = metadata;
        this.authors = authors;
        this.license = license;
    }

    /**
     * protected default empty constructor, trying to get injectMocks working
     */
    public Media() {}

    public boolean hashTransitions() {
        return (this.opening != null || this.ending != null);
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

    public int getFileStartTime() {
        return fileStartTime;
    }

    public void setFileStartTime(int fileStartTime) {
        this.fileStartTime = fileStartTime;
    }

    public int getDuration() {
        return fileStopTime - fileStartTime;
    }

    /**
     * @deprecated
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFileStopTime() {
        return fileStopTime;
    }

    public void setFileStopTime(int fileStopTime) {
        this.fileStopTime = fileStopTime;
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

        //if null then we are erasing relations between media and transition
        if (opening == null && this.opening != null) {
            if (this.opening.getAfterMediaItem() != null) {
                this.opening.setAfterMediaItem(null);
            }
        }

        this.opening = opening;

        //check that afterMediaItem of transition is THIS.
        if (this.opening != null && this.opening.getAfterMediaItem() != this) {
            this.opening.setAfterMediaItem(this);
        }
    }

    public Transition getEnding() {
        return ending;
    }

    public void setEnding(Transition ending) {

        //if null then we are erasing relations between media and transition
        if (ending == null && this.ending != null) {
            if (this.ending.getBeforeMediaItem() != null) {
                this.ending.setBeforeMediaItem(null);
            }
        }

        this.ending = ending;

        //check that beforerMediaItem of transition is THIS.
        if (this.ending != null && this.ending.getBeforeMediaItem() != this) {
            this.opening.setBeforeMediaItem(this);
        }
    }
}
