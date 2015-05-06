/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
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
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.fx.MusicCatalogPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.MusicCatalogView;
import com.videonasocialmedia.videona.presentation.views.adapter.MusicCatalogAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class MusicCatalogFragment extends Fragment implements MusicCatalogView {

    MusicCatalogAdapter adapter;
    MusicCatalogPresenter presenter;
    private RecyclerView.LayoutManager layoutManager;

    @InjectView(R.id.catalog_recycler)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.inject(this, v);
        if (presenter == null)
            presenter = new MusicCatalogPresenter(this);

        layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void showCatalog(List<Music> elementList) {
        adapter = new MusicCatalogAdapter(elementList);
        adapter.setRecyclerClickListener((RecyclerClickListener) getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void hideError() {

    }

    @Override
    public void showLoadingLabel() {

    }

    @Override
    public void hideActionLabel() {

    }

    @Override
    public boolean isTheListEmpty() {
        return false;
    }

    @Override
    public void appendFx(List<Effect> effectList) {
        //adapter.appendMusicList(effectList);
    }

    public List<Music> getFxList() {
        return adapter.getElementList();
    }


    /**
     * @deprecated
     */
    public MusicCatalogAdapter getAdapter() {
        return adapter;
    }


}
