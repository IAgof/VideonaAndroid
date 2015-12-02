package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.NamePreferencePresenter;

import butterknife.OnClick;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class NamePreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new NamePreferencePresenter(this, sharedPreferences);
        getSupportActionBar().setTitle(R.string.name);
    }

    @Override
    public void putIconForEditTextIsNotNull() {
        editTextImage.setImageResource(R.drawable.gatito_rules_pressed);
    }

    @Override
    public void putIconForEditTextIsNull() {
        editTextImage.setImageResource(R.drawable.gatito_rules);
    }

    @OnClick(R.id.info_field)
    public void removeName() {
        presenter.removeData();
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeName);
    }

}
