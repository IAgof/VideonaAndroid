/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.exceptions.NoMediaInTrackException;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by jca on 30/3/15.
 */
public class MediaTrack extends Track {

    /**
     * Overwritten attribute of the superclass forcing it to be a Media objects list
     */
    private TreeSet<Media> items;
    private long duration;


    public MediaTrack() {
        this.items = new TreeSet<>();
        this.duration = 0;
    }

    public MediaTrack(TreeSet<Media> items) {
        if (items.comparator()!=null&&(items.comparator()) instanceof MediaComparator){
            this.items = items;
        }else{
            this.items=new TreeSet<>(new MediaComparator());
            this.items.addAll(items);
        }
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
class MediaComparator implements Comparator<Media>{
    @Override
    public int compare(Media lhs, Media rhs) {
        int result=0;
        if (lhs.getStartTime()<rhs.getStartTime()){
            result=-1;
        }else if (lhs.getStartTime()>rhs.getStartTime()){
            result=1;
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
