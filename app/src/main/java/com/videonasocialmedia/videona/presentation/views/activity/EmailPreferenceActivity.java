package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EmailPreferencePresenter;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class EmailPreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new EmailPreferencePresenter(this, sharedPreferences);
        getSupportActionBar().setTitle(R.string.emailPreference);
    }

    @Override
    public void putIconForEditTextIsNotNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_email);
    }

    @Override
    public void putIconForEditTextIsNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_email_add);
    }

}
