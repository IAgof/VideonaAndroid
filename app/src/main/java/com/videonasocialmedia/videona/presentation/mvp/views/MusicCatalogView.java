/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;



import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface MusicCatalogView {

    public void showCatalog(List<Music> elementList);

    void showLoading ();

    void hideLoading ();

    void showError (String error);

    void hideError ();

    void showLoadingLabel();

    void hideActionLabel ();

    boolean isTheListEmpty ();

    void appendFx (List<Effect> movieList);
}
