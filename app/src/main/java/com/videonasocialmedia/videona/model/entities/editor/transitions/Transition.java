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
package com.videonasocialmedia.videona.model.entities.editor.transitions;

import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * Transitions are technically effects for video purpose. However in this model transition are handle
 * very different. Regardless of its similar behaviour with the RealmEffect class, Transitions will be the
 * first elements to be applied to the final track. This track need to be composed before any other
 * effect has take place.
 * In addition transitions cannot be layered, they will be always applied before any other effect.
 */
public abstract class Transition extends EditorElement {

    /**
     * Unique effect identifier
     */
    protected String type;

    /**
     * Media after the transition. When defined it will overwrite afterMedia.opening transition.
     */
    private Media afterMediaItem;

    /**
     * Media before the transition. When defined it will overwrite beforeMedia.ending transition.
     */
    private Media beforeMediaItem;

    /**
     * The moment the effect is applied since start of the track.
     */
    protected long startTime;

    /**
     * Duration of the effect.
     */
    protected long duration;

    /**
     * RealmEffect author name.
     */
    protected String authorName;

    /**
     * RealmEffect author instance if it is a videona user, can be null if it is not.
     */
    protected User author;

    /**
     * License os the effect.
     */
    protected License license;

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @param identifier      - Unique identifier of the media for the current project.
     * @param iconPath        - Path to a resource that allows represent the media in the view.
     * @param type            - Opengl unique identifier for transition.
     * @param afterMediaItem  - Media item immediately preceding the transition. If null the
     *                        transition must be the first item of the editor track, and therefore
     *                        it must be added a void media (blackout 1 sec) during assembly
     *                        proccess.
     * @param beforeMediaItem - Media item immediately following the transition. If null the
     *                        transition must be the last item of the editor track, and therefore
     *                        it must be added a void media (blackout 1 sec) during assembly
     *                        proccess.
     * @param duration        - transition elapsed time.
     * @param author          - Transition owner's user reference.
     * @param license         - Owner's choice licensing for the transition.
     */
    public Transition(String identifier, String iconPath, String type, Media afterMediaItem,
                      Media beforeMediaItem, long duration, User author, License license) {
        super(identifier, iconPath);
        this.type = type;
        this.afterMediaItem = afterMediaItem;
        this.beforeMediaItem = beforeMediaItem;
        this.duration = duration;
        this.author = author;
        this.license = license;
    }

    /**
     * @param identifier       - Unique identifier of the media for the current project.
     * @param iconPath         - Path to a resource that allows represent the media in the view.
     * @param selectedIconPath - if not null used as icon when something interact with the element.
     * @param type             - Opengl unique identifier for transition.
     * @param afterMediaItem   - Media item immediately preceding the transition. If null the
     *                         transition must be the first item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param beforeMediaItem  - Media item immediately following the transition. If null the
     *                         transition must be the last item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param duration         - transition elapsed time.
     * @param author           - Transition owner's user reference.
     * @param license          - Owner's choice licensing for the transition.
     */
    public Transition(String identifier, String iconPath, String selectedIconPath, String type,
                      Media afterMediaItem, Media beforeMediaItem, long duration, User author,
                      License license) {
        super(identifier, iconPath, selectedIconPath);
        this.type = type;
        this.afterMediaItem = afterMediaItem;
        this.beforeMediaItem = beforeMediaItem;
        this.duration = duration;
        this.author = author;
        this.license = license;
    }

    //applying methods

    /**
     * This method is called by the editor export functionality when the assembly has been triggered.
     * His functions consist on add the transition between two given media items in the editor track.
     * TODO
     */
    public abstract void doTheMagic();

    /**
     * This method is called in real time by the editor to show a preview of the current edition
     * track modified by the transition.
     * TODO
     */
    public abstract void preview();


    //getters & setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Media getAfterMediaItem() {
        return afterMediaItem;
    }

    public void setAfterMediaItem(Media afterMediaItem) {

        //if null then we are erasing relations between media and transition
        if (afterMediaItem == null && this.afterMediaItem != null) {
            if (this.afterMediaItem.getOpening() != null) {
                this.afterMediaItem.setOpening(null);
            }
        }

        this.afterMediaItem = afterMediaItem;

        //after assigned the media, check if opening is THIS
        if (this.afterMediaItem != null && this.afterMediaItem.getOpening() != this) {
            this.afterMediaItem.setOpening(this);
        }
    }

    public Media getBeforeMediaItem() {
        return beforeMediaItem;
    }

    public void setBeforeMediaItem(Media beforeMediaItem) {

        //if null then we are erasing relations between media and transition
        if (beforeMediaItem == null && this.beforeMediaItem != null) {
            if (this.beforeMediaItem.getEnding() != null) {
                this.beforeMediaItem.setEnding(null);
            }
        }

        this.beforeMediaItem = beforeMediaItem;

        //after assigned the media, check if opening is THIS
        if (this.beforeMediaItem != null && this.beforeMediaItem.getEnding() != this) {
            this.beforeMediaItem.setEnding(this);
        }
    }
}
