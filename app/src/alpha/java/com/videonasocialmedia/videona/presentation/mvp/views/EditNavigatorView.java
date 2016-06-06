package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

/**
 *
 */
public interface EditNavigatorView {
    void enableNavigatorActions();

    void disableNavigatorActions();

    void goToMusic(Music music);
}
