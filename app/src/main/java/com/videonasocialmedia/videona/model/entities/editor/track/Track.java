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
package com.videonasocialmedia.videona.model.entities.editor.track;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A track is ordered container of Media elements that can be moved, resized, or modified with the
 * editor tools.
 */
public abstract class Track {

    /**
     * List of elements that conforms the track in order of sequence.
     */
    protected LinkedList<Media> items;

    /**
     * A list of effects to be applied on the items of the track
     */
    protected ArrayList<Effect> effects;

    /**
     * A collection of transitions to be applied between two items of the track each. It can be
     * mapped using a concatenation of both Media objects in strictly order:
     *      Key = beforeMediaIdentifier+afterMediaIdentifier
     */
    protected HashMap<String, Transition> transitions;
    private long duration;

    /**
     * Default constructor. Called when a new project is created.
     */
    protected Track() {
        this.items = new LinkedList<Media>();
        this.effects = new ArrayList<Effect>();
        this.transitions = new HashMap<String, Transition>();
    }

    /**
     * Track fields constructor. Used when a saved project is launched.
     *
     * @param items - Ordered list of media items.
     * @param effects - List of effects to be applied.
     * @param transitions - Collection of transitions between media elements.
     */
    protected Track(LinkedList<Media> items, ArrayList<Effect> effects,
                    HashMap<String, Transition> transitions) {
        this.items = items;
        this.effects = effects;
        this.transitions = transitions;
    }

    //Media Items
    /**
     * Inserts a new Media item at the given position. The position must be calculate in the view
     * getting the indexes of adjacent items.
     *
     * @param position - index where the item has to be added.
     * @param itemToAdd - the Media item.
     */
    public void insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {

        //Check if possible
        if(items == null){
            this.setItems(new LinkedList<Media>());
        }

        //ensure valid index
        int trackSize = items.size();
        if(position <= 0){
            position = 0;
        } else if(position >= trackSize){
            position = trackSize;
        }

        //Get adjacent items
        Media afterMedia = null;
        try {
            afterMedia = items.get(position);
        } catch(IndexOutOfBoundsException e) {
            //there will be something behind new item.
        }
        Media beforeMedia = null;
        try {
            beforeMedia = items.get(position-1);
        } catch(IndexOutOfBoundsException e) {
            //there will be something before item.
        }

        //Check transitions
        Transition beforeTransition = null;
        if(beforeMedia != null) beforeTransition = beforeMedia.getEnding();
        Transition afterTransition = null;
        if(afterMedia != null) afterTransition = afterMedia.getOpening();
        //There could not separate two media items joined by a transition.
        if(afterTransition == beforeTransition){
            throw new IllegalItemOnTrack("Can not add an item between two items which " +
                    "share a transition. Transition must be disengaged before");
        }

        //add the item.
        this.items.add(position, itemToAdd);

        //Modified affected effects if any
        /**
         * TODO la idea es conservar las posiciones relativas originales de start y finish time dentro
         * de los items originales donde empezaba y/o terminaba el efecto.
         */
    }


    /**
     * Delete Media item. Get his position and deletes from the list.
     *
     * @param itemToDelete - Media item to be deleted.
     * @return TRUE if the list contained the specified element.
     */
    public boolean deleteItem(Media itemToDelete) throws IllegalOrphanTransitionOnTrack {
        //Check if possible
        if(items == null){
            this.setItems(new LinkedList<Media>());
        }

        //Check transition is not violated.
        if(itemToDelete.hashTransitions()){
            throw new IllegalOrphanTransitionOnTrack("Media item to delete must be disengaged " +
                    "from transitions first");
        }

        //try to delete element from list.
        boolean result = items.removeFirstOccurrence(itemToDelete);

        if(result){
            //TODO mover los efectos para que conserven sus posiciones relativas a items.
        }
        return result;
    }

    /**
     * Delete Media item on the given position.
     *
     * @param position
     */
    public boolean deleteItemAt(int position) throws IllegalOrphanTransitionOnTrack {
        return this.deleteItem(items.get(position));
    }

    /**
     * Moves Media item to the given position.
     *
     * @param newPosition - The new position in the track for the media item.
     * @param itemToMove - The media item to ve moved.
     */
    public boolean moveItemTo(int newPosition, Media itemToMove) throws IllegalItemOnTrack,
            IllegalOrphanTransitionOnTrack {
        LinkedList<Media> saveList = items;
        try {
            if(this.deleteItem(itemToMove)) {
                this.insertItemAt(newPosition, itemToMove);
            } else {
                this.setItems(saveList);
                return false;
            }
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            this.setItems(saveList);
            throw illegalItemOnTrack;
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            this.setItems(saveList);
            throw illegalOrphanTransitionOnTrack;
        }
        return true;
    }


    //Effects
    //TODO disntinguir entre audioeffects y media effects.
    public void insertEffect(Effect effect){
    }
    public void deleteEffect(){
    }
    public void moveEffect(){
    }



    //Transitions
    //TODO distinguir entre audio transitions y media transitions.
    public void insertTransitionAfter() {
    }
    public void deleteTransition(){
    }
    public void deleteTransitionAfter(){
    }



    //getter & setters
    public LinkedList<Media> getItems() {
        return items;
    }
    public void setItems(LinkedList<Media> items) {
        this.items = items;
    }
    public ArrayList<Effect> getEffects() {
        return effects;
    }
    public void setEffects(ArrayList<Effect> effects) {
        this.effects = effects;
    }
    public HashMap<String, Transition> getTransitions() {
        return transitions;
    }
    public void setTransitions(HashMap<String, Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * Calculates the time in milliseconds in which the given item starts for this MediaTrack.
     *
     * @param mItem - Media Item from which track start time is required.
     * @return - The time in milliseconds in which the item media starts on the final media track.
     */
    public long getTrackStartTimeFor(Media mItem){
        return 0;
    }

    public long getDuration() {
        long trackDuration = 0;
        for(Media item: this.items){
            trackDuration += item.getDuration();
        }
        return trackDuration;
    }
}
