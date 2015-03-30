/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;


import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;

import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface FxCatalogView{

    public void showCatalog(List<EditorElement> elementList);

    void showLoading ();

    void hideLoading ();

    void showError (String error);

    void hideError ();

    void showLoadingLabel();

    void hideActionLabel ();

    boolean isTheListEmpty ();

    void appendFx (List<Effect> movieList);
}
