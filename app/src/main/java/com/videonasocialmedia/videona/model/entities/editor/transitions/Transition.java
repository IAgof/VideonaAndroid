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

import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * Transitions are technically effects for video purpose. However in this model transition are handle
 * very different. Regardless of its similar behaviour with the Effect class, Transitions will be the
 * first elements to be applied to the final track. This track need to be composed before any other
 * effect has take place.
 * In addition transitions cannot be layered, they will be always applied before any other effect.
 */
public abstract class  Transition {

    /**
     * Unique effect identifier
     */
    protected String name;

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
     * Effect author name.
     */
    protected String authorName;

    /**
     * Effect author instance if it is a videona user, can be null if it is not.
     */
    protected User author;

    /**
     * License os the effect.
     */
    protected License license;

    /**
     * Default constructor. Called when the owner is a videona user.
     *
     * @param name - transition unique identifier.
     * @param afterMediaItem - Media item immediately preceding the transition. If null the
     *                         transition must be the first item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param beforeMediaItem - Media item immediately following the transition. If null the
     *                         transition must be the last item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param duration - transition elapsed time.
     * @param author - Transition owner's user reference.
     * @param license - Owner's choice licensing for the transition.
     */
    public Transition(String name, Media afterMediaItem, Media beforeMediaItem, long duration,
                      User author, License license) {
        this.name = name;
        this.setBeforeMediaItem(beforeMediaItem);
        this.setAfterMediaItem(afterMediaItem);
        this.duration = duration;
        this.author = author;
        this.license = license;
    }

    /**
     * Constructor called when the owner is not a videona user.
     *
     * @param name - transition unique identifier.
     * @param afterMediaItem - Media item immediately preceding the transition. If null the
     *                         transition must be the first item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param beforeMediaItem - Media item immediately following the transition. If null the
     *                         transition must be the last item of the editor track, and therefore
     *                         it must be added a void media (blackout 1 sec) during assembly
     *                         proccess.
     * @param duration - transition elapsed time.
     * @param authorName - Transition owner's name. Used when owner is not a videona user.
     * @param license - Owner's choice licensing for the trnasition.
     */
    public Transition(String name, Media afterMediaItem, Media beforeMediaItem, long duration,
                      String authorName, License license) {
        this.name = name;
        this.setBeforeMediaItem(beforeMediaItem);
        this.setAfterMediaItem(afterMediaItem);
        this.duration = duration;
        this.authorName = authorName;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
        this.afterMediaItem = afterMediaItem;
        if(afterMediaItem != null) afterMediaItem.setOpening(this);
    }
    public Media getBeforeMediaItem() {
        return beforeMediaItem;
    }
    public void setBeforeMediaItem(Media beforeMediaItem) {
        this.beforeMediaItem = beforeMediaItem;
        if(beforeMediaItem != null) beforeMediaItem.setEnding(this);
    }
}
