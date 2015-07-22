package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.Fragment;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class VideoTimeLineFragment extends Fragment implements VideoTimeLineView {

    @InjectView(R.id.catalog_recycler)
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
        ButterKnife.inject(this, v);
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
                .getDrawable(R.drawable.common_icon_delete_normal);
        callback = new ItemTouchHelperCallback(adapter, trashIcon);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showVideoList(List<Video> videoList) {
        if (adapter == null || touchHelper == null || callback == null || recyclerView == null)
            initRecycler();
        adapter.setVideoList(videoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
