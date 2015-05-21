/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import android.test.AndroidTestCase;

import com.videonasocialmedia.videona.model.entities.editor.media.Media;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class GetMediaListFromProjectUseCaseTest extends AndroidTestCase {

    GetMediaListFromProjectUseCase useCase;

    public GetMediaListFromProjectUseCaseTest(){
        useCase = new GetMediaListFromProjectUseCase();
    }

    public void testGetMediaListFromProject(){

        Method method = null;

        try {
            method = GetMediaListFromProjectUseCase.class.getDeclaredMethod("getMediaListFromProject");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);

        LinkedList<Media> result = null;
        try {
            result = (LinkedList < Media> ) method.invoke(useCase);
        } catch(InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            // generic exception handling
            e.getCause().printStackTrace();
        }

        LinkedList<Media> anyResult = new LinkedList<Media>();
        assertNotNull(result);
        assertEquals(result,anyResult);

    }
}
