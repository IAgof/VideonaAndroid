package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.views.EditNavigatorView;

/**
 *
 */
public class EditNavigatorPresenter {

    EditNavigatorView navigatorView;
    Project project;

    public EditNavigatorPresenter(EditNavigatorView navigatorView) {
        this.navigatorView = navigatorView;
        project = Project.getInstance(null, null, null);
        areThereVideosInProject();
    }

    public void areThereVideosInProject() {
        if (project.getMediaTrack().getNumVideosInProject() > 0)
            navigatorView.enableNavigatorActions();
        else
            navigatorView.disableNavigatorActions();
    }

}
