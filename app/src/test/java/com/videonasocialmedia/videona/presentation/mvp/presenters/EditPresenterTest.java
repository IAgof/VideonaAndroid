package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videona.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by jliarte on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class EditPresenterTest {
    @InjectMocks private EditPresenter editPresenter;
    @Mock private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    @Mock private EditorView editorView;

    @Before
    public void injectTestDoubles() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadProjectCallsGetMusicFromProjectUseCase() {
        editPresenter = new EditPresenter(editorView, null);

        editPresenter.loadProject();

        Mockito.verify(getMusicFromProjectUseCase).getMusicFromProject(editPresenter);
    }
}
