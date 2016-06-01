package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicDetailView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicDetailActivity extends VideonaActivity implements MusicDetailView {

    public static String KEY_MUSIC_ID = "KEY_MUSIC_ID";

    @Bind(R.id.music_title)
    TextView musicTitle;
    @Bind(R.id.music_author)
    TextView musicAuthor;
    @Nullable
    @Bind(R.id.music_image)
    ImageView musicImage;
    @Nullable
    @Bind(R.id.scene_root)
    FrameLayout sceneRoot;
    @Bind(R.id.detail_content)
    ViewGroup detailContent;

    private Scene acceptCancelScene;
    private Scene deleteSecene;

    private MusicDetailPresenter musicDetailPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        initToolbar();

        musicDetailPresenter = new MusicDetailPresenter(this);

        try {
            Bundle extras = this.getIntent().getExtras();
            int musicId = extras.getInt(KEY_MUSIC_ID);

            musicDetailPresenter.onCreate(musicId);

        } catch (Exception e) {
            //TODO show snackbar with error message
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
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

    @Override
    public void showImage(int resourceId) {
        musicImage.setImageResource(resourceId);
    }

    @Override
    public void setupScene(boolean musicInProject) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            acceptCancelScene = Scene.getSceneForLayout(sceneRoot,
                    R.layout.activity_music_detail_scene_accept_cancel, this);
            deleteSecene = Scene.getSceneForLayout(sceneRoot,
                    R.layout.activity_music_detail_scene_delete, this);
            if (musicInProject) {
                TransitionManager.go(deleteSecene);
            } else {
                TransitionManager.go(acceptCancelScene);
            }

        } else {
            LayoutInflater inflater = this.getLayoutInflater();
            if (musicInProject) {
                inflater.inflate(R.layout.activity_music_detail_scene_delete, sceneRoot);
            } else {
                inflater.inflate(R.layout.activity_music_detail_scene_accept_cancel, sceneRoot);
            }
        }
        ButterKnife.bind(this);
    }

    @Override
    public void showBackground(int colorResourceId) {
        detailContent.setBackgroundResource(colorResourceId);
    }

    @Nullable
    @OnClick(R.id.select_music)
    public void selectMusic() {
        musicDetailPresenter.addMusic();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.go(deleteSecene);
        } else {
            LayoutInflater inflater = this.getLayoutInflater();
            inflater.inflate(R.layout.activity_music_detail_scene_delete, sceneRoot);
        }
        ButterKnife.bind(this);
    }

    @Nullable
    @OnClick(R.id.cancel_music)
    public void back() {
        finish();
    }

    @Nullable
    @OnClick(R.id.delete_music)
    public void deleteMusic() {
        musicDetailPresenter.removeMusic();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.go(acceptCancelScene);
        } else {
            LayoutInflater inflater = this.getLayoutInflater();
            inflater.inflate(R.layout.activity_music_detail_scene_accept_cancel, sceneRoot);
        }
        ButterKnife.bind(this);
    }
}
