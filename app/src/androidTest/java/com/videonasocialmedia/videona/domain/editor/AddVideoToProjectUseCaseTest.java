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

import org.mockito.ArgumentCaptor;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * This class tests the add video to project use case.
 */
public class AddVideoToProjectUseCaseTest extends AndroidTestCase {

    AddVideoToProjectUseCase useCase;
    OnAddMediaFinishedListener listener;

    public AddVideoToProjectUseCaseTest() {
        useCase = new AddVideoToProjectUseCase();
        listener = mock(OnAddMediaFinishedListener.class);
    }

    /**
     * This class is used to check if the listener of add video use case is calling some method
     * when a list of paths of video files is passed it.
     */
    public void testAddMediaItemsToProject() {
        ArrayList<String> list = new ArrayList<>();
        list.add("path/to/video");
        list.add("path/to/video");

        //useCase.addMediaItemsToProject(list, listener);

        ArgumentCaptor<MediaTrack> fooCaptor = ArgumentCaptor.forClass(MediaTrack.class);
        //verify(listener, atLeastOnce()).onAddMediaItemToTrackSuccess(fooCaptor.capture());
        verify(listener, never()).onAddMediaItemToTrackError();
    }

    /**
     * This class is used to check if the use case returns a boolean when try to insert item in
     * mediatrack.
     */
    public void testAddMediaItemToTrack() throws NoSuchMethodException,
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
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            // generic exception handling
            e.getCause().printStackTrace();
        }

        boolean anyBoolean = true;
        assertSame(anyBoolean, result);
    }



}
