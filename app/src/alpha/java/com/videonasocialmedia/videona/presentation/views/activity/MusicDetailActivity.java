package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MusicDetailActivity extends VideonaActivity implements MusicDetailView {

    public static String AUTHOR_EXTRAS_KEY = "AUTHOR_EXTRAS_KEY";
    public static String TITLE_EXTRAS_KEY = "TITLE_EXTRAS_KEY";
    public static String IMAGE_EXTRAS_KEY = "IMAGE_EXTRAS_KEY";
    public static String MUSIC_EXTRAS_KEY = "MUSIC_EXTRAS_KEY";


    @Bind(R.id.music_title)
    TextView musicTitle;
    @Bind(R.id.music_author)
    TextView musicAuthor;
    @Bind(R.id.music_image)
    ImageView musicImage;
    @Bind(R.id.select_music)
    ImageButton selectMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        try {
            Bundle extras = this.getIntent().getExtras();
            String author = extras.getString(AUTHOR_EXTRAS_KEY);
            String title = extras.getString(TITLE_EXTRAS_KEY);
            int imageId = extras.getInt(IMAGE_EXTRAS_KEY);
            int musicId = extras.getInt(MUSIC_EXTRAS_KEY);

            showAuthor(author);
            showTitle(title);
            showImage(imageId);

        } catch (Exception e) {
            //TODO show snackbar with error message
            selectMusic.setEnabled(false);
        }
    }

    @Override
    public void showAuthor(String author) {
        musicAuthor.setText(author);
    }

    @Override
    public void showTitle(String title) {
        musicTitle.setText(title);
    }

    @Override
    public void showImage(String imagePath) {

        Glide.with(this).load(imagePath).into(musicImage);
    }

    public void showImage(int resourceId) {
        musicImage.setImageResource(resourceId);
    }
}
