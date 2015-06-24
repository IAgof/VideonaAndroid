/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.initapp;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.videona.presentation.views.activity.InitAppActivity;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Loading project use case
 * <p/>
 * //TODO Make different state test project (startNewProject, deleteProject, restartProject, ...)
 */
public class LoadingProjectUseCaseTest extends ActivityInstrumentationTestCase2<InitAppActivity> {

    LoadingProjectUseCase useCase;

    OnInitAppEventListener listener;

    InitAppActivity activity;

    Context context;


    public LoadingProjectUseCaseTest() {
        super(InitAppActivity.class);

    }


    public void setUp() throws Exception {

        activity = getActivity();
        context = activity.getApplicationContext();

    }


    public void testStartNewProject() {

        assertNotNull(activity);
        assertNotNull(context);

        useCase = new LoadingProjectUseCase(context);

        listener = mock(OnInitAppEventListener.class);

        useCase.checkProjectState(listener);

        verify(listener, atLeastOnce()).onLoadingProjectSuccess();
        verify(listener, never()).onLoadingProjectError();

    }


}
