package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoGalleryAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnTransitionClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemSelectionSupport;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.MultiItemSelectionSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryFragment extends VideonaFragment implements VideoGalleryView,
        RecyclerViewClickListener, OnTransitionClickListener {

    public static final int SELECTION_MODE_SINGLE = 0;
    public static final int SELECTION_MODE_MULTIPLE = 1;
    public static final int GALLERY_COLS_LANDSCAPE = 6;
    private static final int GALLERY_COLS_PORTRAIT = 4;
    protected TimeChangesHandler timeChangesHandler = new TimeChangesHandler();
    protected VideoGalleryAdapter videoGalleryAdapter;
    protected VideoGalleryPresenter videoGalleryPresenter;
    protected Video selectedVideo;
    protected int folder;
    protected OnSelectionModeListener onSelectionModeListener;
    protected ItemSelectionSupport clickSupport;
    protected MultiItemSelectionSupport selectionSupport;
    protected int selectionMode;
    @Bind(R.id.catalog_recycler)
    RecyclerView recyclerView;

    public static VideoGalleryFragment newInstance(int folder, int selectionMode) {
        VideoGalleryFragment videoGalleryFragment = new VideoGalleryFragment();
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
        ButterKnife.bind(this, v);
        if (videoGalleryPresenter == null)
            videoGalleryPresenter = new VideoGalleryPresenter(this);
        int galleryColumns = GALLERY_COLS_LANDSCAPE;
        if (((VideonaActivity)this.getActivity()).isPortraitOriented()) {
            galleryColumns = GALLERY_COLS_PORTRAIT;
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), galleryColumns,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickSupport = ItemSelectionSupport.addTo(recyclerView);
        clickSupport.setOnItemClickListener(new ItemSelectionSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (selectionSupport.getChoiceMode() == MultiItemSelectionSupport.ChoiceMode.MULTIPLE)
                    if (selectionSupport.isItemChecked(position)) {
                        selectionSupport.setItemChecked(position, true);
                        onSelectionModeListener.onItemUnchecked();
                    } else {
                        selectionSupport.setItemChecked(position, false);
                        onSelectionModeListener.onItemChecked();
                    }
            }
        });
        clickSupport.setOnItemLongClickListener(new ItemSelectionSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                if (selectionMode == SELECTION_MODE_MULTIPLE)
                    selectionSupport.setChoiceMode(MultiItemSelectionSupport.ChoiceMode.MULTIPLE);
                else
                    selectionSupport.setChoiceMode(MultiItemSelectionSupport.ChoiceMode.SINGLE);

                selectionSupport.setItemChecked(position, true);
                onSelectionModeListener.onItemChecked();
                return true;
            }
        });
        selectionSupport = MultiItemSelectionSupport.addTo(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        videoGalleryPresenter.start();
        videoGalleryPresenter.obtainVideos(folder);
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
        ButterKnife.unbind(this);
        timeChangesHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        videoGalleryAdapter.setSelectionSupport(selectionSupport);
        videoGalleryAdapter.setRecyclerViewClickListener(this);
        recyclerView.setAdapter(videoGalleryAdapter);
        videoGalleryAdapter.setOnTransitionClickListener(this);

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
    public void showVideoTimeline() {
    }

    private void showTimeTag(final List<Video> videoList) {
        Runnable updateVideoTime = new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                for (int index = 0; index < videoList.size(); index++) {
                    Video video = videoList.get(index);
                    try {
                        //Log.d("SHOW TIME TAG", "" + index);
                        retriever.setDataSource(video.getMediaPath());
                        String duration = retriever.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_DURATION);
                        int durationInt = Integer.parseInt(duration);
                        video.setDuration(durationInt);
                        video.setStopTime(durationInt);
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

    @Override
    public void onClick(int position) {
        selectedVideo = videoGalleryAdapter.getVideo(position);
        //videoGalleryAdapter.notifyDataSetChanged();
    }

    public Video getSelectedVideo() {
        return selectedVideo;
    }

    public void updateView() {
        List<Video> selectedVideos = getSelectedVideoList();
        if(selectedVideos.size() > 0) {
            for (Video video:selectedVideos) {
                videoGalleryAdapter.removeVideo(video);
            }
            videoGalleryAdapter.clearView();
        }
    }

    public List<Video> getSelectedVideoList() {
        List<Video> result = new ArrayList<>();
        SparseBooleanArray selectedElements = selectionSupport.getCheckedItemPositions();
        for (int i = 0; selectedElements != null && i < videoGalleryAdapter.getItemCount(); i++) {
            if (selectedElements.get(i)) {
                result.add(videoGalleryAdapter.getVideo(i));
            }
        }
        return result;
    }

    @Override
    public void onClick(View transitionOrigin, int positionOnAdapter) {
        selectedVideo = videoGalleryAdapter.getVideo(positionOnAdapter);
        String videoPath = selectedVideo.getMediaPath();
       /* Intent i = new Intent(getActivity(), VideoFullScreenPreviewActivity.class);
        i.putExtra("VIDEO_PATH", videoPath);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(),
                            new Pair<View, String>(transitionOrigin, "preview of one video"));
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }*/
    }

    protected class TimeChangesHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            videoGalleryAdapter.notifyItemChanged(msg.what);
        }
    }
}
