package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
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

    public void checkMusicAndNavigate() {
        GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        getMusicFromProjectUseCase.getMusicFromProject(new GetMusicFromProjectCallback() {
            @Override
            public void onMusicRetrieved(Music music) {
                navigatorView.goToMusic(music);
            }
        });
    }

}
