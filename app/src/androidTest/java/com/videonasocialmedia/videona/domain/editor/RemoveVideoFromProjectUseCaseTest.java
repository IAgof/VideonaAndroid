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

import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.videona.model.entities.social.User;

import org.mockito.ArgumentCaptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * This class tests the remove video from project use case.
 */
public class RemoveVideoFromProjectUseCaseTest extends AndroidTestCase {

    RemoveVideoFromProjectUseCase useCase;
    OnRemoveMediaFinishedListener listener;
    Media media;
    String videoPath;
    ArrayList<User> authors;

    // TODO: we need to access to the Project to obtain the mediatrack, because this test returns
    // null value for it when try to remove the media item

    public RemoveVideoFromProjectUseCaseTest() {
        listener = mock(OnRemoveMediaFinishedListener.class);
        authors = new ArrayList<>();
        authors.add(new User("user"));
        videoPath = "path/to/video";
        media = new Video(null, null, "path/to/video", 0, 0, authors, null);
    }

    /**
     * This class is used to check if the listener of remove video use case is calling some method
     * when a list of media files is passed it.
     */
    public void testRemoveMediaItemsFromProject() {
        ArrayList<Media> list = new ArrayList<>();
        list.add(media);

        useCase.removeMediaItemsFromProject(list, listener);

        ArgumentCaptor<MediaTrack> fooCaptor = ArgumentCaptor.forClass(MediaTrack.class);
        verify(listener, atLeastOnce()).onRemoveMediaItemFromTrackSuccess(fooCaptor.capture());
        verify(listener, never()).onRemoveMediaItemFromTrackError();
    }

    /**
     * This class is used to check if the use case returns a boolean when try to delete item in
     * mediatrack.
     */
    public void testRemoveMediaItemFromTrack() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        Method method = null;
        try {
            method = RemoveVideoFromProjectUseCase.class.getDeclaredMethod("removeMediaItemFromTrack", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);

        boolean result = false;
        try {
            result = (boolean) method.invoke(useCase, media);
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
