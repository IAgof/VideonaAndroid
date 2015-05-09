/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.record.LoadingProjectUseCase;

public class LoadingProjectPresenter implements onLoadingProjectFinishedListener {

    private LoadingProjectUseCase loadingProjectUseCase;

    public LoadingProjectPresenter(){

        loadingProjectUseCase = new LoadingProjectUseCase();

    }


    public void startLoadingProject(){

        loadingProjectUseCase.checkProjectState(this);

    }

    @Override
    public void onLoadingProjectSuccess() {

        //TODO navigate to last activity saved

    }
}
