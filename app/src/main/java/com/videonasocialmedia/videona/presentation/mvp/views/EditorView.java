/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.media.Music;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditorView {

    //void initVideoPlayer(String videoPath);

    void initMusicPlayer(Music music);

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void showError(int causeTextResource);

    void showMessage(int stringToast);

    void refreshStartTimeTag(int time);

    void refreshStopTimeTag(int time);

    void refreshDurationTag(int duration);

    void enableMusicPlayer(Music music);

    void disableMusicPlayer();

}
