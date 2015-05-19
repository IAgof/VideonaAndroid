/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.record;


//TODO move to correct folder path. By default, app begins in record activity. During SplashScreen makes LoadingProjectUseCase

import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.presenters.onLoadingProjectFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;

public class LoadingProjectUseCase {


    public LoadingProjectUseCase(){


    }

    public void checkProjectState(onLoadingProjectFinishedListener listener){
    //public void checkProjectState(){

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
        //TODO Define path project. By default, path app. ¿Path .temp, private data

        Project.getInstance(Constants.PROJECT_TITLE, Constants.PATH_PROJECT, checkProfile());


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
