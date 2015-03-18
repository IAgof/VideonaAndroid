/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;


import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface FxCatalogView{

    public void showCatalog(List<com.videonasocialmedia.videona.model.entities.editor.Effect> movieList);

    void showLoading ();

    void hideLoading ();

    void showError (String error);

    void hideError ();

    void showLoadingLabel();

    void hideActionLabel ();

    boolean isTheListEmpty ();

    void appendFx (List<com.videonasocialmedia.videona.model.entities.editor.Effect> movieList);
}
