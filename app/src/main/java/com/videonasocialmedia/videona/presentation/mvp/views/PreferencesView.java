/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.views;

import android.preference.ListPreference;

import java.util.ArrayList;

/**
 * This interface is used to show the setting menu.
 */
public interface PreferencesView {
    /**
     * This method sets the available settings supported by the device
     *
     * @param preference
     * @param listNames
     * @param listValues
     */
    void setAvailablePreferences(ListPreference preference, ArrayList<String> listNames, ArrayList<String> listValues);

    /**
     * This method sets the default settings supported by the device
     *
     * @param preference
     * @param name the value for the key
     * @param key the key of the preference
     */
    void setDefaultPreference(ListPreference preference, String name, String key);

    /**
     * Sets the actual settings selected by the user
     *
     * @param preference
     * @param name
     */
    void setPreference(ListPreference preference, String name);

    /**
     * Sets the summary value of a preference
     *
     * @param key
     * @param value
     */
    void setSummary(String key, String value);

    void configSignIn(boolean signedIn);
}
