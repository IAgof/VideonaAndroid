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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.Effect;
import com.videonasocialmedia.videona.presentation.mvp.views.FxCatalogView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class FxCatalogFragment extends Fragment implements FxCatalogView{


    @InjectView(R.id.catalog_recycler) RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void showCatalog(List<Effect> movieList) {

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


}
