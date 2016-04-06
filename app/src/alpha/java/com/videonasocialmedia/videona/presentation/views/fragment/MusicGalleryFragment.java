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
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is used to show the music gallery.
 */
public class MusicGalleryFragment extends VideonaFragment implements MusicGalleryView {

    @Bind(R.id.catalog_recycler)
    RecyclerView recyclerView;

    private MusicGalleryAdapter musicGalleryAdapter;
    private MusicGalleryPresenter musicGalleryPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.bind(this, v);
        if (musicGalleryPresenter == null)
            musicGalleryPresenter = new MusicGalleryPresenter(this);
        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        musicGalleryAdapter.setMusicRecyclerViewClickListener((MusicRecyclerViewClickListener) this.getActivity());
        recyclerView.setAdapter(musicGalleryAdapter);
    }

    @Override
    public void reloadMusic(List<Music> musicList) {
        musicGalleryAdapter.setMusicRecyclerViewClickListener((MusicRecyclerViewClickListener) this.getActivity());
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

    /**
     * Returns the list of the available songs
     *
     * @return
     */
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
