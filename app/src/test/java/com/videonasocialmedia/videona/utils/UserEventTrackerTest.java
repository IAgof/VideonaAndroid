package com.videonasocialmedia.videona.utils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by jliarte on 7/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserEventTrackerTest {

    @Mock
    private MixpanelAPI mockedMixpanelAPI;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void constructorSetsMixpanelProperty() {
        UserEventTracker userEventTracker = new UserEventTracker(mockedMixpanelAPI);

        assertThat("Mixpanel is set", userEventTracker.mixpanel, is(mockedMixpanelAPI));
        assertThat("Mixpanel object type", userEventTracker.mixpanel, instanceOf(MixpanelAPI.class));
    }

    @Test
    public void getInstanceReturnsATrackerObject() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);

        assertThat("Mixpanel is set", userEventTracker, notNullValue());
        assertThat("Mixpanel object type", userEventTracker, instanceOf(UserEventTracker.class));
    }

    @Test
    public void getInstanceReturnsSameTrackerObject() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        UserEventTracker userEventTracker2 = UserEventTracker.getInstance(mockedMixpanelAPI);

        assertThat("Same instances", userEventTracker, is(userEventTracker2));
    }

    @Test
    public void trackEventCallsMixpanelTrack() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);

        UserEventTracker.Event event = new UserEventTracker.Event(AnalyticsConstants.VIDEO_EDITED, null);
        userEventTracker.trackEvent(event);

        Mockito.verify(mockedMixpanelAPI).track(event.getName(), event.getProperties());
    }

    @Captor
    ArgumentCaptor<UserEventTracker.Event> eventCaptor;

    @Test
    public void trackClipsReorderedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(UserEventTracker.getInstance(mockedMixpanelAPI));
        Project videonaProject = getAProject();

        userEventTracker.trackClipsReordered(videonaProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_REORDER));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(), videonaProject);
    }

    @Test
    public void trackClipTrimmedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(UserEventTracker.getInstance(mockedMixpanelAPI));
        Project videonaProject = getAProject();

        userEventTracker.trackClipTrimmed(videonaProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_TRIM));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(), videonaProject);
    }

    @Test
    public void trackClipSplitCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(UserEventTracker.getInstance(mockedMixpanelAPI));
        Project videonaProject = getAProject();

        userEventTracker.trackClipSplitted(videonaProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_SPLIT));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(), videonaProject);
    }

    @Test
    public void trackClipDuplicatedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(UserEventTracker.getInstance(mockedMixpanelAPI));
        Project videonaProject = getAProject();
        int copies = 3;

        userEventTracker.trackClipDuplicated(copies, videonaProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_DUPLICATE));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.NUMBER_OF_DUPLICATES), is(copies));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(), videonaProject);
    }

    @Test
    public void trackMusicAddedCallsTrackWithEventNameAndProperties() throws IllegalItemOnTrack, JSONException {
        UserEventTracker userEventTracker = Mockito.spy(UserEventTracker.getInstance(mockedMixpanelAPI));
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author");
        videonaProject.getAudioTracks().get(0).insertItem(music);

        userEventTracker.trackMusicSet(videonaProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_MUSIC_SET));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.MUSIC_TITLE), is(music.getTitle()));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(), videonaProject);
    }

    public void assertEvenPropertiesIncludeProjectCommonProperties(JSONObject eventProperties, Project videonaProject) throws JSONException {
        assertThat(eventProperties.getInt(AnalyticsConstants.NUMBER_OF_CLIPS), is(videonaProject.numberOfClips()));
        assertThat(eventProperties.getInt(AnalyticsConstants.VIDEO_LENGTH), is(videonaProject.getDuration()));
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }
}
