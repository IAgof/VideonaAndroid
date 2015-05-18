/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.initapp;

import android.test.AndroidTestCase;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoadingProjectUseCaseTest extends AndroidTestCase {

    LoadingProjectUseCase useCase;

    OnInitAppEventListener listener;

    public LoadingProjectUseCaseTest(){

        useCase = new LoadingProjectUseCase(getContext());

        listener = mock(OnInitAppEventListener.class);

    }

    public void testStartNewProject(){

        useCase.checkProjectState(listener);

        verify(listener, atLeastOnce()).onLoadingProjectSuccess();

    }

}
