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
public class EmailPreferencePresenter extends EditTextPreferencePresenter {

    public EmailPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                    SharedPreferences sharedPreferences) {
        super(editTextPreferenceView, sharedPreferences);
    }

    @Override
    public void setPreference(String text) {
        editor.putString(ConfigPreferences.EMAIL, text);
        editor.commit();
        editTextPreferenceView.setPreferenceToMixpanel("account_email", text);
        if(isValidEmail(text))
            editTextPreferenceView.goBack();
        else
            editTextPreferenceView.showMessage(R.string.invalid_email);
    }

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(ConfigPreferences.EMAIL, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterEmail;
    }

    @Override
    public void removeData() {
        editor.putString(ConfigPreferences.EMAIL, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
