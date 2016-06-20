package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.domain.editor.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.presentation.mvp.views.TrimView;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import org.junit.After;
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
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimPreviewPresenterTest {
    @InjectMocks private TrimPreviewPresenter trimPreviewPresenter;
    @Mock private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    @Mock private TrimView trimView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(trimView, userEventTracker);

        assertThat(trimPreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(trimView, mockedUserEventTracker);
        Project videonaProject = getAProject();

        assertThat(trimPreviewPresenter.currentProject, is(videonaProject));
    }

    @Test
    public void setTrimCallsTracking() {
        Project videonaProject = getAProject();

        trimPreviewPresenter.setTrim(0, 10);

        Mockito.verify(mockedUserEventTracker).trackClipTrimmed(videonaProject);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }
}
