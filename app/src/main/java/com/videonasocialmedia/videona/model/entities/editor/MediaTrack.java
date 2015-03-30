/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.exceptions.NoMediaInTrackException;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jca on 30/3/15.
 */
public class MediaTrack extends Track {

    /**
     * Overwritten attribute of the superclass forcing the key to be a Media object
     */
    TreeMap<Long, Media> items;

    private long duration;

    public TreeMap<Long, Media> getItems() {
        return items;
    }

    public void setItems(TreeMap<Long, Media> items) {
        this.items = items;
    }

    public void putItem(Media mediaItem, long t) {
        items.put(new Long(t), mediaItem);
    }

    public void putAll(Map<Long, Media> items) {
        items.putAll(items);
    }

    /**
     * Look for a Transition before a media Item
     *
     * @param mediaItem
     * @return returns the transitions if exists, null otherwise
     */
    public Transition getTransitionBefore(Media mediaItem) {
        // TODO search the transition
        return null;
    }

    /**
     * Look for a Transition after a media Item
     *
     * @param mediaItem
     * @return returns the transitions if exists, null otherwise
     */
    public Transition getTransitionAfter(Media mediaItem) {
        // TODO search the transition
        return null;
    }

    /**
     * add a transition object to the track
     * If one or the two of the media objects in the transition are not in the track an Exception is thrown
     *
     * @param transition
     */
    public void addTransition(Transition transition) throws NoMediaInTrackException {
        //TODO check if the medias are in track and are consecutives
        this.transitions.add(transition);
    }

    public long getDuration() {
        return duration;
    }

}
