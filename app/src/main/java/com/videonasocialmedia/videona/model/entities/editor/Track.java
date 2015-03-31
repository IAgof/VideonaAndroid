/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public abstract class Track {

    ArrayList<Object> items;

    /**
     * A list of effects to be applied on the items of the track
     */
    ArrayList<Effect> effects;

    /**
     * A list of effects to be applied between two items of the track each
     */
    ArrayList<Transition> transitions;

    public ArrayList<Effect> getEffects() {
        return effects;
    }

    public void setEffects(ArrayList<Effect> effects) {
        this.effects = effects;
    }

    /**
     * returns the effects applied in an exact moment
     * @param t the time in milliseconds
     * @return the effect applied at t milliseconds from start of the track
     */
    public List<Effect> getEffects(long t){
        //TODO search the effects
        return effects;
    }

    public void addEffect (Effect effect){
        effects.add(effect);
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }


    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }


}
