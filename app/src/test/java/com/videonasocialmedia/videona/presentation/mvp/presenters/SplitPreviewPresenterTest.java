package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.media.MediaMetadataRetriever;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.SplitView;
import com.videonasocialmedia.videona.test.shadows.MediaMetadataRetrieverShadow;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by jliarte on 10/06/16.
 */
// TODO(jliarte): 15/06/16 I need to use robolectric here as Video is being copied in the presenter
@RunWith(RobolectricTestRunner.class)
public class SplitPreviewPresenterTest {
    @Mock private SplitView mockedSplitView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

    // TODO(jliarte): 13/06/16 Decouple Video entity from android
    @Mock(name="retriever") MediaMetadataRetriever mockedMediaMetadataRetriever;
    @InjectMocks Video injectedVideo;

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
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView, userEventTracker);

        assertThat(presenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView, mockedUserEventTracker);

        assertThat(presenter.currentProject, is(videonaProject));
    }

    @Test
    @Config(shadows = {MediaMetadataRetrieverShadow.class})
    public void splitVideoCallsUserTracking() {
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView, mockedUserEventTracker);
        Project videonaProject = getAProject();

        presenter.splitVideo(injectedVideo, 0, 10);

        Mockito.verify(mockedUserEventTracker).trackClipSplitted(videonaProject);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }
}
