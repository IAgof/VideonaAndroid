/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.MusicGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicGalleryView;
import com.videonasocialmedia.videona.presentation.views.adapter.MusicGalleryAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show the music gallery.
 */
public class MusicGalleryFragment extends Fragment implements MusicGalleryView {

    @InjectView(R.id.catalog_recycler)
    RecyclerView recyclerView;
    private MusicGalleryAdapter musicGalleryAdapter;
    private MusicGalleryPresenter musicGalleryPresenter;
    private RecyclerView.LayoutManager layoutManager;
    private Music selectedMusic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.inject(this, v);
        if (musicGalleryPresenter == null)
            musicGalleryPresenter = new MusicGalleryPresenter(this);
        layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        musicGalleryPresenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMusic(List<Music> musicList) {
        musicGalleryAdapter = new MusicGalleryAdapter(musicList);
        musicGalleryAdapter.setRecyclerViewClickListener((RecyclerViewClickListener) this.getActivity());
        recyclerView.setAdapter(musicGalleryAdapter);
    }

    @Override
    public void reloadMusic(List<Music> musicList) {
        musicGalleryAdapter.setRecyclerViewClickListener((RecyclerViewClickListener) this.getActivity());
        recyclerView.setAdapter(musicGalleryAdapter);
    }

    @Override
    public boolean isTheListEmpty() {
        return (musicGalleryAdapter == null) || musicGalleryAdapter.isMusicListEmpty();
    }

    @Override
    public void appendMusic(List<Music> musicList) {
        musicGalleryAdapter.appendMusic(musicList);
    }

    public List<Music> getMusicList() {
        return musicGalleryAdapter.getElementList();
    }

    /**
     * @deprecated
     */
    public MusicGalleryAdapter getAdapter() {
        return musicGalleryAdapter;
    }
}
