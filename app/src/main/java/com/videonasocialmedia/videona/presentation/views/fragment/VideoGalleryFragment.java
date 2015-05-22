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
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoGalleryAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryFragment extends Fragment implements VideoGalleryView, RecyclerViewClickListener {

    @InjectView(R.id.catalog_recycler)
    RecyclerView recyclerView;
    private VideoGalleryAdapter videoGalleryAdapter;
    private VideoGalleryPresenter videoGalleryPresenter;
    private RecyclerView.LayoutManager layoutManager;
    private Video selectedVideo;
    private int folder;
    //private Bundle args;

    public static VideoGalleryFragment newInstance(int folder) {
        VideoGalleryFragment videoGalleryFragment = new VideoGalleryFragment();
        Bundle args = new Bundle();
        args.putInt("FOLDER", folder);
        videoGalleryFragment.setArguments(args);
        return videoGalleryFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folder= this.getArguments().getInt("FOLDER", VideoGalleryPresenter.EDITED_FOLDER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.inject(this, v);
        if (videoGalleryPresenter == null)
            videoGalleryPresenter = new VideoGalleryPresenter(this);
        layoutManager = new GridLayoutManager(this.getActivity(), 6,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        videoGalleryPresenter.start();
        videoGalleryPresenter.obtainVideos(folder);
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
        ButterKnife.reset(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showVideos(List<Video> videoList) {
        videoGalleryAdapter = new VideoGalleryAdapter(videoList);
        videoGalleryAdapter.setRecyclerViewClickListener(this);
        recyclerView.setAdapter(videoGalleryAdapter);
    }

    @Override
    public boolean isTheListEmpty() {
        return (videoGalleryAdapter == null) || videoGalleryAdapter.isVideoListEmpty();
    }

    @Override
    public void appendVideos(List<Video> videoList) {
        videoGalleryAdapter.appendVideos(videoList);
    }

    @Override
    public void onClick(int position) {
        selectedVideo = videoGalleryAdapter.getVideo(position);
        //videoGalleryAdapter.notifyDataSetChanged();
    }

    public Video getSelectedVideo() {
        return selectedVideo;
    }
}
