/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.initapp;


//TODO move to correct folder path. By default, app begins in record activity. During SplashScreen makes LoadingProjectUseCase

import android.content.Context;

import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnInitAppEventListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.UserPreferences;

public class LoadingProjectUseCase {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Context of application
     */
    private Context context;

    UserPreferences userPreferences;

    public LoadingProjectUseCase(Context context){

        this.context = context;

        userPreferences = new UserPreferences(context);

    }

    public void checkProjectState(OnInitAppEventListener listener){

        if(isProjectStarted()) {

            initProject();
        } else {
            // Default
            startNewProject();
        }

       listener.onLoadingProjectSuccess();
    }

    private void startNewProject(){

        //TODO Define project title (by date, by project count, ...)
        //TODO Define path project. By default, path app. Path .temp, private data


        Project.getInstance(Constants.PROJECT_TITLE, userPreferences.getPrivatePath(), checkProfile());




    }


    //TODO load last project activated
    private void initProject(){

    }


    //TODO Check user profile, by default 720p free
    private Profile checkProfile(){

        return Profile.getInstance(Profile.ProfileType.free);
    }


    //TODO Check if a project has been initialized and start in that state (Future).
    private boolean isProjectStarted(){

        // Default startNewProject()
        return false;
    }

}
