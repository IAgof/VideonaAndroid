package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MusicDetailActivity extends VideonaActivity implements MusicDetailView {

    public static String KEY_AUTHOR_EXTRAS = "KEY_AUTHOR_EXTRAS";
    public static String KEY_TITLE_EXTRAS = "KEY_TITLE_EXTRAS";
    public static String KEY_IMAGE_EXTRAS = "KEY_IMAGE_EXTRAS";
    public static String KEY_MUSIC_ID_EXTRAS = "KEY_MUSIC_ID_EXTRAS";

    @Bind(R.id.music_title)
    TextView musicTitle;
    @Bind(R.id.music_author)
    TextView musicAuthor;
    @Bind(R.id.music_image)
    ImageView musicImage;
    @Bind(R.id.select_music)
    ImageButton selectMusic;

    private MusicDetailPresenter musicDetailPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        initToolbar();

        musicDetailPresenter = new MusicDetailPresenter();

        try {
            Bundle extras = this.getIntent().getExtras();
            String author = extras.getString(KEY_AUTHOR_EXTRAS);
            String title = extras.getString(KEY_TITLE_EXTRAS);
            int imageId = extras.getInt(KEY_IMAGE_EXTRAS);
            int musicId = extras.getInt(KEY_MUSIC_ID_EXTRAS);

            musicDetailPresenter.onCreate(musicId);

        } catch (Exception e) {
            //TODO show snackbar with error message
            selectMusic.setEnabled(false);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
