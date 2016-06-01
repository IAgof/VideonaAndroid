/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;

import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditorView {

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void showError(int causeTextResource);

    void showMessage(int stringToast);

    void bindVideoList(List<Video> movieList);

    void setMusic(Music music);

    void updateProject();

    void enableEditActions();

    void disableEditActions();

}
