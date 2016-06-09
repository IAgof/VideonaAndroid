package com.videonasocialmedia.videona.utils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Project;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jliarte on 7/06/16.
 */
public class UserEventTracker {
    private static UserEventTracker userEventTrackerInstance;
    public MixpanelAPI mixpanel;

    protected UserEventTracker(MixpanelAPI mixpanelAPI) {
        this.mixpanel = mixpanelAPI;
    }

    public static UserEventTracker getInstance(MixpanelAPI mixpanelAPI) {
        if (userEventTrackerInstance == null)
            userEventTrackerInstance = new UserEventTracker(mixpanelAPI);
        return userEventTrackerInstance;
    }

    public void trackEvent(Event event) {
        if (event != null) {
            mixpanel.track(event.getName(), event.getProperties());
        }
    }

    public void trackClipsReordered(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_REORDER);
            eventProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, project.numberOfClips());
            eventProperties.put(AnalyticsConstants.VIDEO_LENGTH, project.getDuration());
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class Event {
        private String name;
        private JSONObject properties;

        public Event(String name, JSONObject properties) {
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public JSONObject getProperties() {
            return properties;
        }
    }
}
