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

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

/**
 * This class is used to show the setting menu.
 */
public class NamePreferencePresenter extends EditTextPreferencePresenter {

    public NamePreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                   SharedPreferences sharedPreferences) {
        super(editTextPreferenceView, sharedPreferences);
    }

    @Override
    public void setPreference(String text) {
        editor.putString(ConfigPreferences.NAME, text);
        editor.commit();
        editTextPreferenceView.setPreferenceToMixpanel("$first_name", text);
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(ConfigPreferences.NAME, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterName;
    }

    @Override
    public void removeData() {
        editor.putString(ConfigPreferences.NAME, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
