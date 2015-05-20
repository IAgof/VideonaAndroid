/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.Context;
import android.os.AsyncTask;

import com.videonasocialmedia.videona.domain.initapp.CheckPathsAppUseCase;
import com.videonasocialmedia.videona.domain.initapp.LoadingProjectUseCase;
import com.videonasocialmedia.videona.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;

/**
 *
 * InitAppPresenter. Presenter to initialize the app.
 *
 * The view part is only a splashScreen.
 *
 * Initialize all use cases needed to start the app.
 *
 */
public class InitAppPresenter  implements OnInitAppEventListener {

    /**
     * InitApp View
     */
    private InitAppView initAppView;

    private LoadingProjectUseCase loadingProjectUseCase;

    private CheckPathsAppUseCase checkPathsAppUseCase;

    private Context context;

    public InitAppPresenter(InitAppView initAppView, Context context){

        this.initAppView = initAppView;
        this.context = context;
        checkPathsAppUseCase = new CheckPathsAppUseCase(context);
        loadingProjectUseCase = new LoadingProjectUseCase(context);

    }

    /**
     * Start presenter
     */
    public void start(){

        checkPathsApp();

    }

    /**
     * Stop presenter
     */
    public void stop(){


    }


    private void startLoadingProject(){

        loadingProjectUseCase.checkProjectState(this);

    }

    private void checkPathsApp(){

        checkPathsAppUseCase.checkPaths(this);
    }

    @Override
    public void onCheckPathsAppSuccess() {

        // Go to starLoadingProject. If not path, not project, waterfall model.
        startLoadingProject();
    }

    @Override
    public void onLoadingProjectSuccess() {

        //TODO navigate to last activity saved or whatever
        //TODO control time splashScreen

        // Dummy wait two seconds to show splashScreen
        SplashScreenTask splashScreenTask = new SplashScreenTask();
        splashScreenTask.execute();

    }


    class SplashScreenTask extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        protected Boolean doInBackground(Void... voids) {

            //boolean loggedIn = isSessionActive();

            try {

                // 3 seconds, time in milliseconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //return loggedIn;
            return true;

        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {

            // Start app in RecordActivity.
            initAppView.navigate(RecordActivity.class);
            //initAppView.navigate(MusicGalleryActivity.class);
        }
    }

}
