package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicGalleryPresenter;

/**
 * Created by Veronica Lago Fominaya on 20/05/2015.
 */
public class MusicGalleryActivity extends Activity {

    MusicGalleryPresenter musicGalleryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_gallery);

        //musicGalleryPresenter = new MusicGalleryPresenter(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //musicGalleryPresenter.start();

    }
}
