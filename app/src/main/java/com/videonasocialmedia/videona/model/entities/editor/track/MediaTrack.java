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
package com.videonasocialmedia.videona.model.entities.editor.track;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A media track instance is a track that can contain video and image media items but no audio
 * media items. There could be just one Media track for project.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
 * Created by dfa on 30/3/15.
 */
public class MediaTrack extends Track {

    /**
     * Constructor of minimum number of parameters. Default constructor.
     * Used when a new project is launched.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     */
    public MediaTrack() {
        super();
    }

    /**
     * Parametrized constructor. Used when a saved project is launched.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     */
    public MediaTrack(LinkedList<Media> items, HashMap<Integer, LinkedList<Effect>> effects,
                      HashMap<String, Transition> transitions) {
        super(items, effects, transitions);
        this.checkItems();
    }

    /**
     * Ensure there are only Media items on items list.
     */
    private void checkItems() {
        for (Media item : items) {
            if (item instanceof Audio) {
                //throw new IllegalItemOnMediaTrack("Cannot add media audio items to a media track");
                this.items.removeFirstOccurrence(item);
            }
        }
    }

    public int getNumVideosInProject() {
        return this.getItems().size();
    }

    /**
     * Insert a new Media item in the media track. Get sure it is not an Audio media item.
     *
     * @throws IllegalItemOnTrack - when trying to add a Audio item on
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     */
    @Override
    public boolean insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {
        if (itemToAdd instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an Audio media item to a MediaTrack.");
        }
        return super.insertItemAt(position, itemToAdd);
    }

    @Override
    public boolean insertItem(Media itemToAdd) throws IllegalItemOnTrack {
        if (itemToAdd instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an Audio media item to a MediaTrack.");
        }
        // With super works, waiting merge model branch to dev return this.insertItem(itemToAdd);
        return super.insertItem(itemToAdd);
    }

    /**
     * Delete Media item. Get his position and deletes from the list.
     *
     * @param itemToDelete - Media item to be deleted.
     * @return TRUE if the list contained the specified element.
     */
    @Override
    public Media deleteItem(Media itemToDelete) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IndexOutOfBoundsException, IllegalItemOnTrack {
        return this.deleteItemAt(this.items.indexOf(itemToDelete));
    }

    /**
     * Delete Media item on the given position.
     *
     * @param position
     */
    @Override
    public Media deleteItemAt(int position) throws IllegalOrphanTransitionOnTrack,
            NoSuchElementException, IllegalItemOnTrack {
        if (this.items.get(position) instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an Audio media item to a MediaTrack.");
        }
        return super.deleteItemAt(position);
    }

    /**
     * Moves Media item to the given position.
     *
     * @param newPosition - The new position in the track for the media item.
     * @param itemToMove  - The media item to ve moved.
     */
    @Override
    public boolean moveItemTo(int newPosition, Media itemToMove) throws IllegalItemOnTrack,
            IllegalOrphanTransitionOnTrack {
        if (itemToMove instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an Audio media item to a MediaTrack.");
        }
        return super.moveItemTo(newPosition, itemToMove);
    }

    /**
     * @param items
     */
    @Override
    public void setItems(LinkedList<Media> items) {
        super.setItems(items);
        this.checkItems();
    }

}
