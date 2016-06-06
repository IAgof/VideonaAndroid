package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.videona.presentation.views.adapter.MusicListAdapter;
import com.videonasocialmedia.videona.presentation.views.customviews.VideonaPlayer;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaPlayerListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class MusicListActivity extends VideonaActivity implements MusicListView,
        MusicRecyclerViewClickListener, VideonaPlayerListener {
    @Bind(R.id.music_list)
    RecyclerView musicList;
    @Bind(R.id.videona_player)
    VideonaPlayer videonaPlayer;

    private MusicListAdapter musicAdapter;
    private MusicListPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        setupToolbar();
        presenter = new MusicListPresenter(this);
        initVideoListRecycler();
        videonaPlayer.initVideoPreview(this);
        videonaPlayer.initPreview(0);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initVideoListRecycler() {
        musicAdapter = new MusicListAdapter();
        musicAdapter.setMusicRecyclerViewClickListener(this);
        presenter.getAvailableMusic();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicList.setLayoutManager(layoutManager);
        musicList.setAdapter(musicAdapter);
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
    public void showVideoList(List<Music> musicList) {
        musicAdapter.setMusicList(musicList);
    }

    @Override
    public void onClick(Music music) {
        Intent i = new Intent(this, MusicDetailActivity.class);
        i.putExtra(MusicDetailActivity.KEY_MUSIC_ID, music.getMusicResourceId());
        startActivity(i);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
