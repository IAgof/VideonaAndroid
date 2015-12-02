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

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videona.presentation.mvp.views.EditTextPreferenceView;

/**
 * This class is used to show the setting menu.
 */
public abstract class EditTextPreferencePresenter {

    protected Context context;
    protected SharedPreferences sharedPreferences;
    protected EditTextPreferenceView editTextPreferenceView;
    protected SharedPreferences.Editor editor;

    /**
     * Constructor
     *
     * @param editTextPreferenceView
     * @param context
     * @param sharedPreferences
     */
    public EditTextPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                       Context context, SharedPreferences sharedPreferences) {
        this.editTextPreferenceView = editTextPreferenceView;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void setPreference(String text) {}

    public String getPreviousText() {
        return null;
    }

    public String getHintText() {
        return null;
    }

    public void removeData() {}

}
