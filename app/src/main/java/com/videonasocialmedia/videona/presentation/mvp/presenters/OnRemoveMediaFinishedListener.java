/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

/**
 * This interface is used for monitoring when the items have been deleted from the actual track.
 */
public interface OnRemoveMediaFinishedListener {
    /**
     * This method is used when fails to deleted items from the track.
     */
    void onRemoveMediaItemFromTrackError();

    /**
     * This method is used when items have been deleted from the track.
     *
     * @param mediaTrack the actual track without the items deleted
     */
    void onRemoveMediaItemFromTrackSuccess(MediaTrack mediaTrack);
}

