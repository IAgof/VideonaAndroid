package com.videonasocialmedia.videona.utils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    public void  trackClipsReordered() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        UserEventTracker trackerSpy = Mockito.spy(userEventTracker);
        Project videonaProject = Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));

        trackerSpy.trackClipsReordered(videonaProject);

        Mockito.verify(trackerSpy).trackEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getName(), is(AnalyticsConstants.VIDEO_EDITED));
        JSONObject eventProperties = eventCaptor.getValue().getProperties();
        try {
            assertThat(eventProperties.getString(AnalyticsConstants.EDIT_ACTION), is(AnalyticsConstants.EDIT_ACTION_REORDER));
            assertThat(eventProperties.getInt(AnalyticsConstants.NUMBER_OF_CLIPS), is(videonaProject.numberOfClips()));
            assertThat(eventProperties.getInt(AnalyticsConstants.VIDEO_LENGTH), is(videonaProject.getDuration()));
        } catch (JSONException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
