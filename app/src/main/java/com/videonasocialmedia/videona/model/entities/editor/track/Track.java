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
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

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
     * A collection of lists of effects to be applied on the items of the track, can be mapped by
     * layer.
     */
    protected HashMap<Integer, LinkedList<Effect>> effects;

    /**
     * A collection of transitions to be applied between two items of the track each. It can be
     * mapped using a concatenation of both Media objects in strictly order:
     *      Key = beforeEditorElementIdentifier+afterEditorElementIdentifier
     */
    protected HashMap<String, Transition> transitions;

    /**
     * TODO eliminar, creoq ue aquí no tiene sentido IMHO. O pensarselo para no tener que calcularlo cada vez, por ejemplo que se calcule siempre que se añada un item al track.
     */
    private long duration;

    /**
     * Constructor of minimum number of parameters. Default constructor. Called when a new project
     * is created.
     */
    protected Track() {
        this.items = new LinkedList<Media>();
        this.effects = new HashMap<Integer, LinkedList<Effect>>();
        this.transitions = new HashMap<String, Transition>();
    }

    /**
     * Track fields constructor. Used when a saved project is launched.
     *
     * @param items - Ordered list of media items.
     * @param effects - List of effects to be applied.
     * @param transitions - Collection of transitions between media elements.
     */
    protected Track(LinkedList<Media> items,  HashMap<Integer, LinkedList<Effect>> effects,
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
     * @return
     * @throws IllegalItemOnTrack
     */
    public boolean insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {

        //Check if possible
        if(this.items == null){
            //TODO ¿hemos perdido el track? ¿que hacemos? ¿lo recuperamos de la última versión buena? ¿petamos?
            //por el momento evitamos un nullpointer.
            this.setItems(new LinkedList<Media>());
        }

        //ensure valid index
        int trackSize = items.size();
        if(position <= 0){
            position = 0;
        } else if(position >= trackSize){
            position = trackSize;
        }

        if((this.effects.isEmpty() && this.transitions.isEmpty()) || this.items.isEmpty()){
            //If there are no items on the track it cannot contain effects or transitions
            //if there is not effects and/or transitions then the item could be added without
            //further calculations.
            this.items.add(position, itemToAdd);
            return true;
        } else {
            //if there is

            //Get adjacent items. Util for both checkings: transitions and effects.
            Media afterMedia;
            try {
                afterMedia = items.get(position);
            } catch (IndexOutOfBoundsException e) {
                //adding to the end of the list
                afterMedia = null;
            }
            Media beforeMedia;
            try {
                beforeMedia = items.get(position - 1);
            } catch (IndexOutOfBoundsException e) {
                //adding to beggining of the list.
                beforeMedia = null;
            }

            if (afterMedia == beforeMedia) {
                //is empty WTF!!!
                return false;
            }

            /**
             * TODO transitions no entra en el deadline del 7 de mayo. Pero abrá que hacerlo
             */

            //Adapt events to the new track configuration.
            this.updateEffectsForAddItem(itemToAdd, this.getTrackStartTimeFor(afterMedia));

            //add the item.
            this.items.add(position, itemToAdd);
            return true;
        }
    }

    /**
     *
     * @param itemToAdd
     * @return
     * @throws IllegalItemOnTrack
     */
    public boolean insertItem(Media itemToAdd) throws IllegalItemOnTrack {
        //Check if possible
        if(this.items == null){
            //TODO ¿hemos perdido el track? ¿que hacemos? ¿lo recuperamos de la última versión buena? ¿petamos?
            //por el momento evitamos un nullpointer.
            this.setItems(new LinkedList<Media>());
        }

        return this.insertItemAt(this.items.size(), itemToAdd);
    }


    /**
     * Delete Media item. Get his position and deletes from the list.
     *
     * @param itemToDelete - Media item to be deleted.
     * @return TRUE if the list contained the specified element.
     */
    public Media deleteItem(Media itemToDelete) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IndexOutOfBoundsException, IllegalItemOnTrack {
        return this.deleteItemAt(items.indexOf(itemToDelete));
    }

    /**
     * Delete Media item on the given position.
     *
     * @param position
     */
    public Media deleteItemAt(int position) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IllegalItemOnTrack {

        //Make it possible
        if(this.items == null){
            //TODO ¿hemos perdido el track? ¿que hacemos? ¿lo recuperamos de la última versión buena? ¿petamos?
            //por el momento evitamos un nullpointer.
            this.setItems(new LinkedList<Media>());
            throw new NoSuchElementException();
        }

        if(this.items.isEmpty()){
            //nothing to delete.
            throw new NoSuchElementException();
        }

        //Check transition is not violated.
        if(this.items.get(position).hashTransitions()){
            throw new IllegalOrphanTransitionOnTrack("Media item to delete must be disengaged " +
                    "from transitions first");
        }

        //Get adjacent items. Util for both checkings: transitions and effects.
        Media afterMedia;
        try {
            afterMedia = items.get(position);
        } catch (IndexOutOfBoundsException e) {
            //adding to the end of the list
            afterMedia = null;
        }
        Media beforeMedia;
        try {
            beforeMedia = items.get(position - 1);
        } catch (IndexOutOfBoundsException e) {
            //adding to beggining of the list.
            beforeMedia = null;
        }

        if (afterMedia == beforeMedia) {
            //is empty WTF!!!
            return null;
        }

        //Check to update effects.
        this.updateEffectsForDeleteItem(this.items.get(position));

        //try to delete element from list.
        return items.remove(position);
    }

    /**
     * Moves Media item to the given position.
     *
     * @param newPosition - The new position in the track for the media item.
     * @param itemToMove - The media item to ve moved.
     */
    public boolean moveItemTo(int newPosition, Media itemToMove) throws IllegalItemOnTrack,
            IllegalOrphanTransitionOnTrack {

        //Make it possible
        if(this.items == null){
            //TODO ¿hemos perdido el track? ¿que hacemos? ¿lo recuperamos de la última versión buena? ¿petamos?
            //por el momento evitamos un nullpointer.
            this.setItems(new LinkedList<Media>());
            throw new NoSuchElementException(); //nothing to move.
        }

        if(this.items.isEmpty()){
            //Nothing to move.
            throw new NoSuchElementException();
        }

        //Check transition is not violated.
        if(itemToMove.hashTransitions()){
            throw new IllegalOrphanTransitionOnTrack("Media item to move must be disengaged from " +
                    "transitions first");
        }



        //check for effects

        //apply update as is delete the item
        ArrayList<Effect> removedEffects = this.updateEffectsForDeleteItem(itemToMove);

        //delete the item.
        this.items.remove(this.items.indexOf(itemToMove));

        //insert the item again in his new position.
        this.insertItemAt(newPosition, itemToMove);

        //add the previously deleted effects.
        //TODO cuando sepamos como se insertan los efectos y que esto no va a petar.

        return true;
    }


    //Effects
    //TODO disntinguir entre audioeffects y media effects. No entran en el 7 de mayo.
    public void insertEffect(Effect effect){
    }
    public void deleteEffect(){
    }
    public void moveEffect(){
    }



    //Transitions
    //TODO distinguir entre audio transitions y media transitions. No entran en el 7 de mayo
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
    public HashMap<Integer, LinkedList<Effect>> getEffects() {
        return effects;
    }
    public void setEffects(HashMap<Integer, LinkedList<Effect>> effects) {
        this.effects = effects;
    }
    public HashMap<String, Transition> getTransitions() {
        return transitions;
    }
    public void setTransitions(HashMap<String, Transition> transitions) {
        this.transitions = transitions;
    }

    //Local Utilities

    /**
     * Recalculate effects positions for the new track configuration after item has been removed.
     *
     * @param itemToAdd - Media Item to be added to the track, used to recalculated new effects
     *                  startTime and durations.
     */
    private void updateEffectsForAddItem(Media itemToAdd, long newItemTrackStartTime) {
        for(LinkedList<Effect> layer: this.effects.values()){
            for(Effect e: layer){
                if(newItemTrackStartTime >= e.getStartTime()+e.getDuration()){
                    //case insert item after the effect, we don't have to do anything
                    //continue;

                } else if(newItemTrackStartTime <= e.getStartTime()){
                    //case insert item before the effect. Add newItemDuration to effectStartTime
                    e.setStartTime(e.getStartTime()+itemToAdd.getDuration());
                } else {
                    //case insert in the middle of an effect afected area. We extend the effect
                    //to cover the new added item.
                    e.setDuration(e.getDuration()+itemToAdd.getDuration());
                }
            }
        }
    }

    /**
     * Recalculate effects positions for the new track configuration after item has been removed.
     *
     * @param itemToDelete - Media item to be deleted. Used to recalculate startTimes an durations for
     *                     effects in the new track configuration.
     *
     * @return An ArrayList<Effect> containing effects that has been removed from the track due to
     * the action being performed. If action was a delteItem ignore this return, if action was a move
     * item use this list to add the effects again in his new location after moving the item.
     */
    private ArrayList<Effect> updateEffectsForDeleteItem(Media itemToDelete) {
        long itemStartTime = this.getTrackStartTimeFor(itemToDelete);
        long itemEndTime = itemStartTime+itemToDelete.getDuration();
        ArrayList<Effect> removedEffects = new ArrayList<Effect>();
        for(LinkedList<Effect> layer: this.effects.values()){
            for(Effect e: layer){
                if(itemStartTime > e.getStartTime()+e.getDuration()){
                    //case effect occurs before the item. Nothing to do.
                    //continue;

                } else if(itemEndTime < e.getStartTime()){
                    //case effect occurs after the item. Deduct itemToDelete duration from effect startTime
                    e.setStartTime(e.getStartTime()-itemToDelete.getDuration());

                } else if(itemStartTime <= e.getStartTime() && itemEndTime >= e.getStartTime()+e.getDuration()){
                    //case effect is contained in item. Delete effect.
                    //remove the item and store in a temporal list used for the moveItem action.
                    removedEffects.add(layer.remove(layer.indexOf(e)));
                    //set startTime relative to media, in case of moveItem action. Used later to stablish the new effect startTime.
                    e.setStartTime(e.getStartTime()-itemStartTime);

                } else if(itemStartTime > e.getStartTime() && itemEndTime < e.getStartTime()+e.getDuration()){
                    //case effect contains item. Set new effect duration deducting itemToDelete duration.
                    e.setDuration(e.getDuration()-itemToDelete.getDuration());

                } else if(itemStartTime <= e.getStartTime() && itemEndTime < e.getStartTime()+e.getDuration()){
                    //case effect starts during itemToDelete => Effect new startTime = itemToDelete startTime
                    e.setStartTime(itemStartTime);

                } else if(itemStartTime > e.getStartTime() && itemEndTime >= e.getStartTime()+e.getDuration()){
                    //case effect ends during itemToDelete => Trim effect duration to itemToDelete startTime
                    e.setDuration(itemStartTime-e.getStartTime());

                }
            }
        }
        return removedEffects;
    }

    /**
     * Calculates the time in milliseconds in which the given item starts for this MediaTrack.
     *
     * @param mItem - Media Item from which track start time is required.
     * @return - The time in milliseconds in which the item media starts on the final media track.
     */
    public long getTrackStartTimeFor(Media mItem) throws IndexOutOfBoundsException {

        long result = 0;

        //if the list is empty we choose to return start of track.
        if(this.items == null || this.items.isEmpty()){
            return 0;
        }


        int position = this.items.indexOf(mItem);
        if(mItem == null){
            //items cannot be empty so a mediaItem null means the final of the list
            position = this.items.size();
        } else if(position <=0){
            //if we don't find the mediaItem then return the start of the track.
            //besides, probably a error has occurred.
            return 0;
        }

        //finally if all goes well calculate the actual startTime for the media item given.
        for(Media m: this.items.subList(0, position)){
            result +=m.getDuration();
        }
        return result;
    }

    public long getDuration() {
        long trackDuration = 0;
        for(Media item: this.items){
            trackDuration += item.getDuration();
        }
        return trackDuration;
    }
}