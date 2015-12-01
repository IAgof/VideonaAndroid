package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.UsernamePreferencePresenter;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class UsernamePreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new UsernamePreferencePresenter(this, context, sharedPreferences);
        getSupportActionBar().setTitle(R.string.username);
    }

}
