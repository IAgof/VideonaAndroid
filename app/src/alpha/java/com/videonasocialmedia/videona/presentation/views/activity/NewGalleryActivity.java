package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.view.Window;

import com.videonasocialmedia.videona.R;

/**
 * Created by videona on 9/06/16.
 */
public class NewGalleryActivity extends VideonaActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_gallery);


    }
}
