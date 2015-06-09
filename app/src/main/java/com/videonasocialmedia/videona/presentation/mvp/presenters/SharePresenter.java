/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.videona.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareView;
import com.videonasocialmedia.videona.presentation.views.activity.ShareActivity;

import java.util.LinkedList;

public class SharePresenter {


    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();


    /**
     * Get media list from project use case
     */
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;


    /**
     * Constructor
     *
     */
    public SharePresenter(){
    }


    /**
     * on Create Presenter
     */
    public void onCreate() {

        // Add videoRecorded to EditActivity, only one media
        //TODO do not use static variable videoRecorded
    }

    /**
     * on Start Presenter
     */
    public void onStart() {
        // TODO edit use case onStart
    }


    /**
     * on Resume Presenter
     */
    public void onResume() {
        // TODO edit use case onResume
    }


    /**
     * on Pause Presenter
     */
    public void onPause() {

        // TODO edit use case onPause
    }

    /**
     * on Restart Presenter
     */
    public void onRestart() {

        // TODO edit use case onRestart
    }

    /**
     * on Stop Presenter
     */
    public void onStop() {

        // TODO edit use case onStop
    }

}
