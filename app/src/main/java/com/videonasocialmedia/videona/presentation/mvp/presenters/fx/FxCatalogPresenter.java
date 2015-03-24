/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters.fx;

import com.videonasocialmedia.videona.model.entities.editor.AudioEffect;
import com.videonasocialmedia.videona.model.entities.editor.Effect;
import com.videonasocialmedia.videona.presentation.mvp.views.FxCatalogView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan Javier Cabanas on 23/3/15.
 */
public class FxCatalogPresenter {

    private FxCatalogView fxCatalogView;

    public FxCatalogPresenter(FxCatalogView fxCatalogView) {
        this.fxCatalogView = fxCatalogView;
    }

    public void start(){
        //TODO ask upper layers for fxList and remove the next

        List<Effect> fxList = new ArrayList<>();

        for (int i=0; i<20; i++){
            fxList.add(new AudioEffect());
        }

        OnFxListReceived(fxList);
    }

    //TODO make the next method a listener of use case events
    public void OnFxListReceived(List<Effect> fxList){

        fxCatalogView.showCatalog(fxList);
    }
}
