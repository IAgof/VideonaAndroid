package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnTransitionClickListener;
import com.videonasocialmedia.videona.utils.TimeUtils;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.MultiItemSelectionSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;
    private MusicRecyclerViewClickListener musicRecyclerViewClickListener;
    private OnTransitionClickListener onTransitionClickListener;
    private MultiItemSelectionSupport selectionSupport;

    private int selectedVideoPosition = -1;

    public VideoGalleryAdapter(List<Video> videoList) {
        this.videoList = videoList;

    }

    public void setOnTransitionClickListener(OnTransitionClickListener onTransitionClickListener) {
        this.onTransitionClickListener = onTransitionClickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_gallery_video_item, viewGroup, false);

        this.context = viewGroup.getContext();
        return new VideoViewHolder(rowView, musicRecyclerViewClickListener, onTransitionClickListener);
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
        if(selectionSupport!=null) {
            holder.overlay.setActivated(selectionSupport.isItemChecked(position));
            holder.overlayIcon.setActivated(selectionSupport.isItemChecked(position));
        }
        String duration = TimeUtils.toFormattedTime(selectedVideo.getDuration());
        holder.duration.setText(duration);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

//    public List<Video> getVideoList() {
//        return videoList;
//    }

    public Video getVideo(int position) {
        return videoList.get(position);
    }

    public void setRecyclerViewClickListener(MusicRecyclerViewClickListener musicRecyclerViewClickListener) {
        this.musicRecyclerViewClickListener = musicRecyclerViewClickListener;
    }

    public void setSelectionSupport(MultiItemSelectionSupport selectionSupport) {
        this.selectionSupport = selectionSupport;
    }

    public void appendVideos(List<Video> videoList) {
        this.videoList = new ArrayList<>();
        this.videoList.addAll(videoList);
    }

    public boolean isVideoListEmpty() {
        return videoList.isEmpty();
    }

    public void removeVideo(Video videoToRemove) {
        int indexOfVideoToRemove = videoList.indexOf(videoToRemove);
        videoList.remove(videoToRemove);
        notifyItemRemoved(indexOfVideoToRemove);
    }

    public void clearView() {
        selectionSupport.clearChoices();
    }


    class VideoViewHolder extends RecyclerView.ViewHolder{ //implements View.OnTouchListener {

        MusicRecyclerViewClickListener onClickListener;
        OnTransitionClickListener onTransitionClickListener;

        @Bind(R.id.gallery_thumb)
        ImageView thumb;

        @Bind(R.id.gallery_duration)
        TextView duration;

        @Bind(R.id.gallery_overlay)
        RelativeLayout overlay;

        @Bind(R.id.gallery_overlay_icon)
        ImageView overlayIcon;

        public VideoViewHolder(View itemView, MusicRecyclerViewClickListener onClickListener,
                               OnTransitionClickListener onTransitionClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onClickListener = onClickListener;
            this.onTransitionClickListener = onTransitionClickListener;

        }

        public void setOnTransitionClickListener(OnTransitionClickListener onTransitionClickListener) {
            this.onTransitionClickListener = onTransitionClickListener;
        }

        @OnClick(R.id.video_item)
        public void startVideoPreview(View v) {
            if (selectionSupport.getChoiceMode() == MultiItemSelectionSupport.ChoiceMode.NONE) {
                if(onTransitionClickListener != null)
                    onTransitionClickListener.onClick(itemView, getPosition());
            }
        }

    }

}