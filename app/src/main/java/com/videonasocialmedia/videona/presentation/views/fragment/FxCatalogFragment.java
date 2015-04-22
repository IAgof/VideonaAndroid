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
import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.presentation.mvp.presenters.fx.FxCatalogPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.FxCatalogView;
import com.videonasocialmedia.videona.presentation.views.adapter.FxCatalogAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class FxCatalogFragment extends Fragment implements FxCatalogView{

    FxCatalogAdapter adapter;
    FxCatalogPresenter presenter;
    private RecyclerView.LayoutManager layoutManager;

    @InjectView(R.id.catalog_recycler) RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.inject(this, v);
        presenter = new FxCatalogPresenter(this);

        layoutManager= new GridLayoutManager(this.getActivity(), 5);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void showCatalog(List<EditorElement> elementList) {
        adapter = new FxCatalogAdapter(elementList);
        adapter.setRecyclerClickListener((RecyclerClickListener)getActivity());
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
    public void appendFx(List<Effect> movieList) {

    }

    public List<EditorElement> getFxList(){
        return adapter.getElementList();
    }


}
