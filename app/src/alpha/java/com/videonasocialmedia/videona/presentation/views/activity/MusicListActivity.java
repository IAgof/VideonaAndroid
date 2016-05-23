package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.videona.presentation.views.adapter.MusicListAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class MusicListActivity extends VideonaActivity implements MusicListView,
        MusicRecyclerViewClickListener {
    @Bind(R.id.music_list)
    RecyclerView musicList;
    private MusicListAdapter musicAdapter;
    private MusicListPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);
        presenter = new MusicListPresenter(this);
        initVideoListRecycler();
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
    public void showVideoList(List<Music> musicList) {
        musicAdapter.setMusicList(musicList);
    }

    @Override
    public void onClick(Music music) {
        Intent i = new Intent(this, MusicDetailActivity.class);
        i.putExtra(MusicDetailActivity.MUSIC_ID_EXTRAS_KEY, music.getMusicResourceId());
        i.putExtra(MusicDetailActivity.AUTHOR_EXTRAS_KEY, music.getTitle());
        i.putExtra(MusicDetailActivity.TITLE_EXTRAS_KEY, music.getTitle());
        i.putExtra(MusicDetailActivity.IMAGE_EXTRAS_KEY, music.getIconResourceId());
        startActivity(i);
    }

}
