package com.videonasocialmedia.videona.presentation.views.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Veronica Lago Fominaya on 28/07/2015.
 */
public class AppIntroFragment extends Fragment {

    private int layoutId;

    public static AppIntroFragment newInstance(int layoutId) {
        AppIntroFragment sampleSlide = new AppIntroFragment();
        Bundle args = new Bundle();
        args.putInt("layoutId", layoutId);
        sampleSlide.setArguments(args);
        return sampleSlide;
    }

    public AppIntroFragment() {}

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments() != null && this.getArguments().size() != 0) {
            this.layoutId = this.getArguments().getInt("layoutId");
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);
        return v;
    }

}
