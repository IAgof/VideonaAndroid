/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditorView {

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void showError(int causeTextResource);

    void showMessage(int stringToast);

    void updateProjectDuration(int projectDuration);

    void updateNumVideosInProject(int numVideos);

}
