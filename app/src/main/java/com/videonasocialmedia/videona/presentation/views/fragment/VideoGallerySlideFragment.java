package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryMastersPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnSlideListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by jca on 14/5/15.
 */
public class VideoGallerySlideFragment extends VideoGalleryFragment {

    @InjectView(R.id.button_slide_bottom_panel)
    ImageButton slideButton;

    GalleryMastersPresenter galleryMastersPresenter;
    OnSlideListener onSlideListener;
    RecyclerView.LayoutManager layoutManager;
    boolean up;

    public static VideoGallerySlideFragment newInstance(int folder, int selectionMode) {
        VideoGallerySlideFragment videoGalleryFragment = new VideoGallerySlideFragment();
        Bundle args = new Bundle();
        args.putInt("FOLDER", folder);
        args.putInt("SELECTION_MODE", selectionMode);
        videoGalleryFragment.setArguments(args);
        return videoGalleryFragment;
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        onSelectionModeListener = (OnSelectionModeListener) a;
        onSlideListener = (OnSlideListener) a;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_fragment_slide_catalog, container, false);
        ButterKnife.inject(this, v);
        if (videoGalleryPresenter == null)
            videoGalleryPresenter = new VideoGalleryPresenter(this);
        if (galleryMastersPresenter == null)
            galleryMastersPresenter = new GalleryMastersPresenter(this);

        layoutManager = new GridLayoutManager(this.getActivity(), 5,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        setArrowUp();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        timeChangesHandler.removeCallbacksAndMessages(null);
        onSelectionModeListener.onExitSelection();
    }

    public void loadVideoListToProject(List<Video> videoList) {
        galleryMastersPresenter.loadVideoListToProject(videoList);
    }

    @Override
    public void showVideoTimeline() {
        onSelectionModeListener.onConfirmSelection();
    }

    @OnClick(R.id.button_slide_bottom_panel)
    public void onClick(View view) {
        if (up)
            setArrowDown();
        else
            setArrowUp();
        onSlideListener.onSlide();
    }

    private void setArrowUp() {
        slideButton.setImageResource(R.drawable.activity_edit_icon_arrow_up_normal);
        up =true;
    }

    private void setArrowDown() {
        slideButton.setImageResource(R.drawable.activity_edit_icon_arrow_down_normal);
        up = false;
    }

}
