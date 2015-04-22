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
package com.videonasocialmedia.videona.model.entities.editor.effects;

import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.licensing.License;
import com.videonasocialmedia.videona.model.entities.social.User;

/**
 * Abstract class that represent the general behaviour of an effect than can be applied to a track.
 * An effect has a start time within the track to mark when the effect begins and elapsed time in
 * which it will be applied. I addition has a layer identifier.
 */
public abstract class Effect extends EditorElement {


    /**
     * Effect type identifier.
     */
    protected String type;

    /**
     * Duration of the effect.
     */
    protected long duration;

    /**
     * The moment the effect is applied since start of the track.
     */
    protected long startTime;

    /**
     * Positive integer that defines the layer in which it have to be applied. The lower the layer
     * identifier the most priority it have and the sooner it will be applied in video export
     * process.
     * TODO para los efectos habrá que crearse layers como objetos.
     */
    protected int layer;

    /**
     * Effect author type.
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
     * Constructor called when the effect owner is not a videona user.
     *
     * @param effectType - Effect type identifier.
     * @param layer - Layer identifier that keeps the order in which the effect has to be apply.
     * @param startTime - Time in seconds within result track in which the effect should start to be
     *                  apply.
     * @param duration - Time during which the effect should be apply.
     * @param authorName - Effects owner type. Used to keep a reference for effects created by users
     *                   outside the videona platform.
     */
    protected Effect(String effectType, int layer, long startTime, long duration, String authorName) {
        this.setType(effectType);
        this.setDuration(duration);
        this.setStartTime(startTime);
        this.setLayer(layer);
        this.setAuthorName(authorName);
        this.setAuthor(null);
        this.license = new License(License.CC40_NAME, License.CC40_TEXT);
    }

    /**
     * Default constructor. Called when the effect owner is a videona user.
     *
     * @param effectType - Effect type identifier.
     * @param layer - Layer identifier that keeps the order in which the effect has to be apply.
     * @param startTime - Time in seconds within result track in which the effect should start to be
     *                  apply.
     * @param duration - Time during which the effect should be apply.
     * @param author - Effects owner instance.
     */
    protected Effect(String effectType, int layer, long startTime, long duration, User author) {
        this.setType(effectType);
        this.setDuration(duration);
        this.setStartTime(startTime);
        this.setLayer(layer);
        this.setAuthor(author);
        this.setAuthorName(author.getName());
        this.license = new License(License.CC40_NAME, License.CC40_TEXT);
    }


    //applying methods
    /**
     * This method is called by the editor export functionality when the assembly has been triggered.
     * His functions consist on modify the portion of the final video track that the effect affects.
     *
     * @param target - the media file to be modified by the effect.
     */
    public abstract void doTheMagic(Media target);

    /**
     * This method is called in real time by the editor to show a preview of the current edition
     * track modified by the effect.
     * TODO Aún no se ha pensado como se hará esto.
     */
    public abstract void preview();


    //getters & setters
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
    public int getLayer() {
        return layer;
    }
    public void setLayer(int layer) {
        this.layer = layer;
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
