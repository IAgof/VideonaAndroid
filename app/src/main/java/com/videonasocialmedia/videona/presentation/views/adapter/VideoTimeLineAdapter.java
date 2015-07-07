package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoTimeLinePresenter;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.MovableItemsAdapter;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jca on 6/7/15.
 */
public class VideoTimeLineAdapter extends RecyclerView.Adapter<VideoTimeLineAdapter.VideoViewHolder>
        implements MovableItemsAdapter {

    private Context context;
    private List<Video> videoList;
    private VideoTimeLinePresenter presenter;


    public VideoTimeLineAdapter(List<Video> videoList, VideoTimeLinePresenter presenter) {
        setHasStableIds(true);
        this.videoList = videoList;
        this.presenter=presenter;
    }

    @Override
    public void moveItem(int fromPositon, int toPosition) {
        if (fromPositon != toPosition) {
            Collections.swap(videoList, fromPositon, toPosition);
            notifyItemMoved(fromPositon, toPosition);
        }
    }

    @Override
    public void finishMovement(int newPosition) {
        presenter.moveItem(videoList.get(newPosition),newPosition);
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public long getItemId(int position) {
        return videoList.get(position).getUniqueId();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_videotimeline_video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video selectedVideo = videoList.get(position);
        String path = selectedVideo.getIconPath() != null
                ? selectedVideo.getIconPath() : selectedVideo.getMediaPath();
        Glide.with(context)
                .load(path)
                .centerCrop()
                .error(R.drawable.fragment_gallery_no_image)
                .into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public Video getItem(int position) {
        return videoList.get(position);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.timelinevideo_thumb)
        ImageView thumb;
        @InjectView(R.id.container)
        RelativeLayout container;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
