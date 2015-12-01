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

/**
 * This interface is used to show the setting menu.
 */
public interface EditTextPreferenceView {
    /**
     * Sets the actual user account data
     *
     * @param propertie
     * @param value
     */
    void setPreferenceToMixpanel(String propertie, String value);
}
