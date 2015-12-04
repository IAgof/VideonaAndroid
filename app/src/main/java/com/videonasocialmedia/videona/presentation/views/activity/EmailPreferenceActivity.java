package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.text.InputType;

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
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeEmail);
    }

}
