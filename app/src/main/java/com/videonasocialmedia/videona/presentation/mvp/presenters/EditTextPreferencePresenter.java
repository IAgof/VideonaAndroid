/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videona.presentation.mvp.views.EditTextPreferenceView;

/**
 * This class is used to show the setting menu.
 */
public abstract class EditTextPreferencePresenter {

    protected SharedPreferences sharedPreferences;
    protected EditTextPreferenceView editTextPreferenceView;
    protected SharedPreferences.Editor editor;

    /**
     * Constructor
     *
     * @param editTextPreferenceView
     * @param sharedPreferences
     */
    public EditTextPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                       SharedPreferences sharedPreferences) {
        this.editTextPreferenceView = editTextPreferenceView;
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void setPreference(String text) {}

    public String getPreviousText() {
        return null;
    }

    public int getHintText() {
        return -1;
    }

    public void removeData() {}

}
