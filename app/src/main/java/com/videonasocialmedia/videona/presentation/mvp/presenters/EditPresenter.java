/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.videonasocialmedia.videona.model.entities.social.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.fragment.AudioFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.LookFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.ScissorsFxMenuFragment;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoFxMenuFragment;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class EditPresenter {

    private EditorView editorView;

    private MediaPlayer videoPlayer;
    private MediaPlayer audioPlayer;

    public EditPresenter(EditorView editorView){
        this.editorView=editorView;
    }


    public void start(){
        //TODO load edition project
    }
}
