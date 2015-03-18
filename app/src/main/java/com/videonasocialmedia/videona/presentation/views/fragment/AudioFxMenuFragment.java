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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.FxMenuView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class AudioFxMenuFragment extends Fragment implements FxMenuView {

    @InjectView(R.id.boton_audio_prueba) ImageButton imgButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment_sound, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.boton_audio_prueba)
    public void showLog() {
        Log.d("Fragment Audio", "He pulsado el primer botón");
        imgButton.setImageResource(R.drawable.activity_edit_icon_transition_normal);

        //TODO poner el fragment de abajo
    }

    @Override
    public void showItemList(List<ImageButton> buttonList) {
        //TODO mostrar la lista dinámica de botones
    }
}
