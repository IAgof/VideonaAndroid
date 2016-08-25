/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.videona.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 24/08/16.
 */
public class ShareVideoPresenterTest {

    @Mock
    private MixpanelAPI mockedMixpanelAPI;
    @Mock
    private ShareVideoView mockedShareVideoView;
    @Mock
    private UserEventTracker mockedUserEventTracker;
    @Mock
    private SharedPreferences mockSharedPrefs;


    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void constructorSetsCurrentProject() {

        Project videonaProject = getAProject();
        ShareVideoPresenter shareVideoPresenter = new ShareVideoPresenter(mockedShareVideoView,
                mockedUserEventTracker, mockSharedPrefs);

        assertThat(shareVideoPresenter.currentProject, is(videonaProject));
    }

    @Test
    public void constructorSetsUserTracker() {

        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        ShareVideoPresenter shareVideoPresenter = new ShareVideoPresenter(mockedShareVideoView,
                userEventTracker, mockSharedPrefs);

        assertThat(shareVideoPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void shareVideoPresenterCallsTracking(){

        ShareVideoPresenter shareVideoPresenter = new ShareVideoPresenter(mockedShareVideoView,
                mockedUserEventTracker, mockSharedPrefs);
        Project videonaProject = getAProject();
        String socialNetwokId = "SocialNetwork";
        int totalVideosShared = 0;
        shareVideoPresenter.trackVideoShared(socialNetwokId);

        Mockito.verify(mockedUserEventTracker).trackVideoShared(socialNetwokId,videonaProject,
                totalVideosShared);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }


}
