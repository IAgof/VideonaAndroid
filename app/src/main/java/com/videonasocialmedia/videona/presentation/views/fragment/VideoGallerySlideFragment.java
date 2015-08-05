package com.videonasocialmedia.videona.presentation.views.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryMastersPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGallerySlideFragment extends VideoGalleryFragment {

    GalleryMastersPresenter galleryMastersPresenter;

    public static VideoGallerySlideFragment newInstance(int folder, int selectionMode) {
        VideoGallerySlideFragment videoGalleryFragment = new VideoGallerySlideFragment();
        Bundle args = new Bundle();
        args.putInt("FOLDER", folder);
        args.putInt("SELECTION_MODE", selectionMode);
        videoGalleryFragment.setArguments(args);
        return videoGalleryFragment;
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 6,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
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
}
