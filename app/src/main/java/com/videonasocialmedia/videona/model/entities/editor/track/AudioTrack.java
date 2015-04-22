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
import com.videonasocialmedia.videona.model.entities.editor.media.Audio;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * An audio track is a track that can only contain Audio media items. There could be many audio
 * tracks in a project.
 *
 * Created by dfa on 30/3/15.
 */
public class AudioTrack extends Track {

    /**
     * Default constructor. Used when a new project is launched.
     */
    public AudioTrack() {
        super();
    }

    /**
     * Parametrized constructor. Used when a saved project is launched.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     */
    public AudioTrack(LinkedList<Media> items, ArrayList<Effect> effects,
                      HashMap<String, Transition> transitions) {
        super(items, effects, transitions);
        this.checkItems();
    }

    /**
     * Ensure there are only Audio items on items list.
     */
    private void checkItems() {
        for(Object item : this.getItems()) {
            if(!(item instanceof Audio)){
                this.items.removeFirstOccurrence(item);
            }
        }
    }

    /**
     * Insert a new Audio item in the AudioTrack. Get sure it is an Audio media item.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     * @throws IllegalItemOnTrack - when trying to add a Audio item on
     */
    @Override
    public void insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {
        if(itemToAdd instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an audio media item to a media track.");
        }
        super.insertItemAt(position, itemToAdd);
    }

    @Override
    public void setItems(LinkedList<Media> items) {
        super.setItems(items);
        this.checkItems();
    }


}
