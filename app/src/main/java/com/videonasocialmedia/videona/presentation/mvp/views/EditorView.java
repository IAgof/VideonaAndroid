/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.editor.Music;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditorView {

    //public void changeBottomFragment(Class<?extends Fragment> fragmentClass);
    //public void changeRightFragment(Class<?extends Fragment> fragmentClass);
    public void navigate();

    public void initVideoPlayer(String videoPath);
    public void initMusicPlayer(Music music);

}
