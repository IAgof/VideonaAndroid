/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
* All rights reserved
*
* Authors:
* Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;

import java.util.ArrayList;

/**
 * This interface is used to add a new media items to the project.
 */
public interface AddMediaToProjectUseCase {
    /**
     * This method is used to add a new media items to the project.
     *
     * @param list     the list of the paths of the new media items
     * @param listener the listener which monitoring when this items have been added to the project
     */
    public void addMediaItemsToProject(ArrayList<String> list, OnAddMediaFinishedListener listener);
}
