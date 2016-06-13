package com.videonasocialmedia.videona.utils;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.model.entities.editor.Project;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jliarte on 7/06/16.
 */
public class UserEventTracker {
    private final String TAG = getClass().getSimpleName();
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

    public static void clear() {
        userEventTrackerInstance = null;
    }

    protected void trackEvent(Event event) {
        if (event != null) {
            mixpanel.track(event.getName(), event.getProperties());
        }
    }

    public void trackClipsReordered(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_REORDER);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipsReordered: error sending mixpanel VIDEO_EDITED reorder event");
            e.printStackTrace();
        }
    }

    public void trackClipTrimmed(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_TRIM);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipTrimmed: error sending mixpanel VIDEO_EDITED trim event");
            e.printStackTrace();
        }
    }

    public void trackClipSplitted(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_SPLIT);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipSplitted: error sending mixpanel VIDEO_EDITED split event");
            e.printStackTrace();
        }
    }

    public void trackClipDuplicated(int copies, Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_DUPLICATE);
            eventProperties.put(AnalyticsConstants.NUMBER_OF_DUPLICATES, copies);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipDuplicated: error sending mixpanel VIDEO_EDITED duplicate event");
            e.printStackTrace();
        }
    }

    public void trackMusicSet(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_MUSIC_SET);
            String musicTitle = "";
            if (project.getMusic() != null) {
                musicTitle = project.getMusic().getTitle();
            }
            eventProperties.put(AnalyticsConstants.MUSIC_TITLE, musicTitle);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addProjectEventProperties(Project project, JSONObject eventProperties) throws JSONException {
        eventProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, project.numberOfClips());
        eventProperties.put(AnalyticsConstants.VIDEO_LENGTH, project.getDuration());
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
