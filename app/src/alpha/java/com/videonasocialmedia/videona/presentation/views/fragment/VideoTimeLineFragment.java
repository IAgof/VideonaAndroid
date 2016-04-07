package com.videonasocialmedia.videona.presentation.views.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoTimeLinePresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoTimeLineView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.ItemTouchHelperCallback;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class VideoTimeLineFragment extends VideonaFragment implements VideoTimeLineView {

    @Bind(R.id.catalog_recycler)
    RecyclerView recyclerView;

    private ItemTouchHelper touchHelper;
    private VideoTimeLineAdapter adapter;
    private VideoTimeLinePresenter presenter;
    private ItemTouchHelper.Callback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_fragment_catalog, container, false);
        ButterKnife.bind(this, v);
        if (presenter == null)
            presenter = new VideoTimeLinePresenter(this);
        initRecycler();
        return v;
    }

    private void initRecycler() {
        adapter = new VideoTimeLineAdapter();
        adapter.setClickListener((VideoTimeLineRecyclerViewClickListener) this.getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(),
                GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Drawable trashIcon = getActivity().getResources()
                .getDrawable(R.drawable.common_icon_delete_media);
        callback = new ItemTouchHelperCallback(adapter, trashIcon);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
        EventBus.getDefault().unregister(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
        if (!EventBus.getDefault().isRegistered(adapter))
            EventBus.getDefault().register(adapter);
    }

    @Override
    public void showVideoList(List<Video> videoList) {
        if (adapter == null || touchHelper == null || callback == null || recyclerView == null)
            initRecycler();
        adapter.setVideoList(videoList);
        adapter.notifyDataSetChanged();
        if (!EventBus.getDefault().isRegistered(adapter)) {
            EventBus.getDefault().register(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public Video getCurrentVideo() {
        int selectedVideoIndex = adapter.getSelectedVideoPosition();
        return adapter.getItem(selectedVideoIndex);
    }

    public int getCurrentPosition() {
        return adapter.getSelectedVideoPosition();
    }
}
