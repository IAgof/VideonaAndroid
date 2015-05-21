/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.ArrayList;

/**
 * This interface is used to delete an existing media items from the project.
 */
public interface RemoveMediaSelectedProjectUseCase {
    /**
     * This method is used to remove media items from the project.
     *
     * @param list     the list of the media items which wants to remove
     * @param listener the listener which monitoring when this items have been deleted from the project
     */
    void removeMediaItemsFromProject(ArrayList<String> list, OnRemoveMediaFinishedListener listener);
}
