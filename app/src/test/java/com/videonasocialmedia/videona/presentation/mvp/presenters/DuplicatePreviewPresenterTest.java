package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.videona.test.shadows.MediaMetadataRetrieverShadow;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(RobolectricTestRunner.class)
//@RunWith(MockitoJUnitRunner.class)
public class DuplicatePreviewPresenterTest {

    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private DuplicateView mockedDuplicateView;
    @Mock private UserEventTracker mockedUserEventTracker;

    // TODO(jliarte): 13/06/16 Decouple Video entity from android
    @Mock(name="retriever") MediaMetadataRetriever mockedMediaMetadataRetriever;

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
        DuplicatePreviewPresenter duplicatePreviewPresenter = new DuplicatePreviewPresenter(mockedDuplicateView, userEventTracker);

        assertThat(duplicatePreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();

        DuplicatePreviewPresenter duplicatePreviewPresenter = new DuplicatePreviewPresenter(mockedDuplicateView, mockedUserEventTracker);

        assertThat(duplicatePreviewPresenter.currentProject, is(videonaProject));
    }

    @Test
    @Config(shadows = {MediaMetadataRetrieverShadow.class})
    public void duplicateVideoCallsTracking() {
        DuplicatePreviewPresenter presenter = new DuplicatePreviewPresenter(mockedDuplicateView, mockedUserEventTracker);
        Project videonaProject = getAProject();
        Video video = new Video("/media/path", 0, 10);
        int numCopies = 3;

        /**
         * Exception accesing in getFileDuration as MediaMetadataRetriever.extractMetadata returns null
         * using a custom shadow instead
         */
        presenter.duplicateVideo(video, 0, numCopies);

        Mockito.verify(mockedUserEventTracker).trackClipDuplicated(numCopies, videonaProject);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }
}
