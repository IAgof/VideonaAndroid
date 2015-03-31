/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.exceptions.NoMediaInTrackException;
import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by jca on 30/3/15.
 */
public class AudioTrack extends Track {

    private TreeSet<Audio> items;
    private long duration;


    public AudioTrack() {
        this.items = new TreeSet<>();
        this.duration = 0;
    }

    public AudioTrack(TreeSet<Audio> items) {
        if (items.comparator() != null && (items.comparator()) instanceof AudioComparator) {
            this.items = items;
        } else {
            this.items = new TreeSet<>(new AudioComparator());
            this.items.addAll(items);
        }
    }

    /**
     * Look for a Transition before a media Item
     *
     * @param audioItem
     * @return returns the transitions if exists, null otherwise
     */
    public Transition getTransitionBefore(Audio audioItem) {
        // TODO search the transition
        return null;
    }

    /**
     * Look for a Transition after a media Item
     *
     * @param audioItem
     * @return returns the transitions if exists, null otherwise
     */
    public Transition getTransitionAfter(Audio audioItem) {
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
}

class AudioComparator implements Comparator<Audio> {
    @Override
    public int compare(Audio lhs, Audio rhs) {
        int result = 0;
        if (lhs.getStartTime() < rhs.getStartTime()) {
            result = -1;
        } else if (lhs.getStartTime() > rhs.getStartTime()) {
            result = 1;
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}