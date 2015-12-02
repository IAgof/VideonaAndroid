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

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

/**
 * This class is used to show the setting menu.
 */
public class UsernamePreferencePresenter extends EditTextPreferencePresenter {

    private String username;

    public UsernamePreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                       Context context, SharedPreferences sharedPreferences) {
        super(editTextPreferenceView, context, sharedPreferences);
    }

    @Override
    public void setPreference(String text) {
        editor.putString(ConfigPreferences.USERNAME, text);
        editor.commit();
        editTextPreferenceView.setPreferenceToMixpanel("$username", text);
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    }

    @Override
    public String getHintText() {
        return context.getResources().getString(R.string.username);
    }

    @Override
    public void removeData() {
        editor.putString(ConfigPreferences.USERNAME, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
