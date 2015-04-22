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
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.transitions.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A media track instance is a track that can contain video and image media items but no audio
 * media items. There could be just one Media track for project.
 *
 * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
 * Created by dfa on 30/3/15.
 */
public class MediaTrack extends Track {

    /**
     * Default constructor. Used when a new project is launched.
     */
    public MediaTrack() {
        super();
    }

    /**
     * Parametrized constructor. Used when a saved project is launched.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     */
    public MediaTrack(LinkedList<Media> items, ArrayList<Effect> effects,
                      HashMap<String, Transition> transitions){
        super(items, effects, transitions);
        this.checkItems();
    }

    /**
     * Ensure there are only Media items on items list.
     */
    private void checkItems(){
        for(Media item : items){
            if(item instanceof Audio){
                //throw new IllegalItemOnMediaTrack("Cannot add media audio items to a media track");
                this.items.removeFirstOccurrence(item);
            }
        }
    }

    /**
     * Insert a new Media item in the media track. Get sure it is not an Audio media item.
     *
     * @see com.videonasocialmedia.videona.model.entities.editor.track.Track
     * @throws IllegalItemOnTrack - when trying to add a Audio item on
     */
    @Override
    public void insertItemAt(int position, Media itemToAdd) throws IllegalItemOnTrack {
        if(itemToAdd instanceof Audio) {
            throw new IllegalItemOnTrack("Cannot add an Audio media item to a MediaTrack.");
        }
        super.insertItemAt(position, itemToAdd);
    }

    @Override
    public void setItems(LinkedList<Media> items) {
        super.setItems(items);
        this.checkItems();
    }


}
