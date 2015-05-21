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

public class CheckPathsAppUseCaseTest extends ActivityInstrumentationTestCase2<InitAppActivity> {

    CheckPathsAppUseCase useCase;

    OnInitAppEventListener listener;

    InitAppActivity activity;

    Context context;


    public CheckPathsAppUseCaseTest(){
        super(InitAppActivity.class);

    }

    public void setUp() throws Exception {

        activity = getActivity();
        context = activity.getApplicationContext();

    }

    public void testCheckPathsApp() {

        assertNotNull(activity);
        assertNotNull(context);

        useCase = new CheckPathsAppUseCase(context);

        listener = mock(OnInitAppEventListener.class);

        useCase.checkPaths(listener);

        verify(listener, atLeastOnce()).onCheckPathsAppSuccess();
        verify(listener, never()).onCheckPathsAppError();
    }

}