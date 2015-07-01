package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoGalleryAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemClickSupport;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemSelectionSupport;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryFragment extends Fragment implements VideoGalleryView, RecyclerViewClickListener {

    public static final int SELECTION_MODE_SINGLE = 0;
    public static final int SELECTION_MODE_MULTIPLE = 1;

    @InjectView(R.id.catalog_recycler)
    RecyclerView recyclerView;
    private TimeChangesHandler timeChangesHandler = new TimeChangesHandler();
    private VideoGalleryAdapter videoGalleryAdapter;
    private VideoGalleryPresenter videoGalleryPresenter;
    private Video selectedVideo;
    private int folder;

    private ItemClickSupport clickSupport;
    private ItemSelectionSupport selectionSupport;

    private int selectionMode;

    public static VideoGalleryFragment newInstance(int folder, int selectionMode) {
        VideoGalleryFragment videoGalleryFragment = new VideoGalleryFragment();
        Bundle args = new Bundle();
        args.putInt("FOLDER", folder);
        args.putInt("SELECTION_MODE", selectionMode);
        videoGalleryFragment.setArguments(args);
        return videoGalleryFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folder = this.getArguments().getInt("FOLDER", VideoGalleryPresenter.EDITED_FOLDER);
        selectionMode = this.getArguments().getInt("SELECTION_MODE", SELECTION_MODE_SINGLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.inject(this, v);
        if (videoGalleryPresenter == null)
            videoGalleryPresenter = new VideoGalleryPresenter(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 6,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickSupport = ItemClickSupport.addTo(recyclerView);
        clickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (selectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.MULTIPLE)
                    if (selectionSupport.isItemChecked(position)) {
                        selectionSupport.setItemChecked(position, true);
                    } else {
                        selectionSupport.setItemChecked(position, false);
                    }
            }
        });
        clickSupport.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                if (selectionMode == SELECTION_MODE_MULTIPLE)
                    selectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                else
                    selectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);

                selectionSupport.setItemChecked(position, true);
                return true;
            }
        });
        selectionSupport = ItemSelectionSupport.addTo(recyclerView);
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
        timeChangesHandler.removeCallbacksAndMessages(null);
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
        showTimeTag(videoList);
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

    private void showTimeTag(final List<Video> videoList) {
        Runnable updateVideoTime = new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                for (int index = 0; index < videoList.size(); index++) {
                    Video video = videoList.get(index);
                    try {
                        Log.d("SHOW TIME TAG", "" + index);
                        retriever.setDataSource(video.getMediaPath());
                        String duration = retriever.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_DURATION);
                        int durationInt = Integer.parseInt(duration);
                        video.setDuration(durationInt);
                        video.setFileDuration(durationInt);
                        video.setFileStopTime(durationInt);
                        timeChangesHandler.sendEmptyMessage(index);
                    } catch (Exception e) {
                        video.setDuration(0);
                    }
                }

            }
        };
        Thread thread = new Thread(updateVideoTime);
        thread.start();
    }

    private class TimeChangesHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            videoGalleryAdapter.notifyItemChanged(msg.what);
        }
    }
}
