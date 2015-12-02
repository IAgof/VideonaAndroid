package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.UsernamePreferencePresenter;

import butterknife.OnClick;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class UsernamePreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new UsernamePreferencePresenter(this, sharedPreferences);
        getSupportActionBar().setTitle(R.string.username);
    }

    @OnClick(R.id.info_field)
    public void removeUsername() {
        presenter.removeData();
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeUsername);
    }

}
