/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ExportProjectUseCaseTest extends ActivityInstrumentationTestCase2<EditActivity> {


    ExportProjectUseCase useCase;

    OnExportFinishedListener listener;

    EditActivity activity;

    Context context;

    public ExportProjectUseCaseTest() {
        super(EditActivity.class);


    }

    public void setUp() throws Exception {

        activity = getActivity();
        context = activity.getApplicationContext();

    }

    public void testExportProjectUseCase() {

        assertNotNull(activity);
        assertNotNull(context);

        useCase = new ExportProjectUseCase(context);

        listener = mock(OnExportFinishedListener.class);

        useCase.exportProject(listener);

        verify(listener, atLeastOnce()).onExportSuccess("");
        verify(listener, never()).onExportError();


    }

}