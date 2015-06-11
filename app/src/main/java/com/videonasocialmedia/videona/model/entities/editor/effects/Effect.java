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
     * Effect type identifier. Uniquely identifies an opengl effect.
     */
    protected String type;

    /**
     * The moment the effect is applied since start of the track.
     */
    protected long startTime;

    /**
     * Duration of the effect.
     */
    protected long duration;

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
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @param identifier - Unique identifier for the editor element for the current project.
     * @param iconPath   - Path to a resource that allows represent the element in the view.
     * @param type       - Opengl unique effect identifier.
     * @param startTime  - Track instant in milliseconds in which the effect has to be applied.
     * @param duration   - Duration of effect.
     * @param license    - Legal stuff.
     * @param author     - Author of the effect.
     */
    protected Effect(String identifier, String iconPath, String type, long startTime, long duration,
                     License license, User author) {
        super(identifier, iconPath);
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.license = license;
        this.author = author;
    }

    /**
     * Parametrized constructor. It requires all possible attributes for an effect object.
     *
     * @param identifier       - Unique identifier for the editor element for the current project.
     * @param iconPath         - Path to a resource that allows represent the element in the view.
     * @param selectedIconPath - if not null used as icon when something interact with the element.
     *                         If null it will be used the iconPath as default.
     * @param type             - Opengl unique effect identifier.
     * @param startTime        - Track instant in milliseconds in which the effect has to be applied.
     * @param duration         - Duration of effect.
     * @param license          - Legal stuff.
     * @param author           - Author of the effect.
     * @param layer            - Number of the layer in which the effect has to be applied. Layers define
     *                         which effects expose to further modifications by others. The lower the layer
     *                         number the soner will be applied in assembly time.
     * @param license          - Legal stuff.
     * @param author           - Author of the effect.
     */
    protected Effect(String iconPath, String selectedIconPath, String identifier, String type,
                     long startTime, long duration, int layer, User author, License license) {
        super(identifier, iconPath, selectedIconPath);
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.layer = layer;
        this.author = author;
        this.license = license;
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
