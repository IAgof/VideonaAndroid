package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.NamePreferencePresenter;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class NamePreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new NamePreferencePresenter(this, sharedPreferences);

        toolbarTitle.setText(R.string.name);
    }

    @Override
    public void putIconForEditTextIsNotNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_person);
    }

    @Override
    public void putIconForEditTextIsNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_person_add);
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeName);
    }

}
