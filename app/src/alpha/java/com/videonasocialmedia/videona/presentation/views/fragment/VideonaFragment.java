package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;

import com.qordoba.sdk.Qordoba;

/**
 * Created by jca on 5/10/15.
 */
public abstract class VideonaFragment extends Fragment{
    @Override
    public void onResume() {
        super.onResume();
        Qordoba.updateScreen(getActivity());
    }
}
