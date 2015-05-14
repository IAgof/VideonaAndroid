/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.domain.editor;

import android.test.AndroidTestCase;

import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;

import org.mockito.ArgumentCaptor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * This class tests the add video to project use case.
 */
public class AddVideoToProjectUseCaseTest extends AndroidTestCase {

    AddVideoToProjectUseCase useCase;
    OnAddMediaFinishedListener listener;
    MediaTrack mediaTrack;

    public AddVideoToProjectUseCaseTest(){
        useCase = new AddVideoToProjectUseCase();
        listener = mock(OnAddMediaFinishedListener.class);
    }


    @Override
    protected void setUp() throws Exception {
        //MockitoAnnotations.initMocks(this);
    }

    public void testAddMediaItemsToProject() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Constants.PATH_APP + File.separator + "V_EDIT_AV2.mp4");
        list.add(Constants.PATH_APP + File.separator + "V_EDIT_AV2.mp4");

        useCase.addMediaItemsToProject(list, listener);

        ArgumentCaptor<MediaTrack> fooCaptor = ArgumentCaptor.forClass(MediaTrack.class);
        verify(listener, atLeastOnce()).onAddMediaItemToTrackSuccess(fooCaptor.capture());
        verify(listener, never()).onAddMediaItemToTrackError();
    }

    public void testAddMediaItemToTrack () throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        String videoPath = "path/to/video";

        Method method = null;
        try {
            method = AddVideoToProjectUseCase.class.getDeclaredMethod("addMediaItemToTrack", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);

        boolean result = false;
        try {
            result = (boolean) method.invoke(useCase, videoPath);
        } catch(InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            // generic exception handling
            e.getCause().printStackTrace();
        }

        boolean anyBoolean = true;
        assertSame(anyBoolean,result);
    }
}
