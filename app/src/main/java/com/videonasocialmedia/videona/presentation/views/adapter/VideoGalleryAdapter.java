package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;
    private RecyclerViewClickListener recyclerViewClickListener;

    private int selectedVideoPosition = -1;

    public VideoGalleryAdapter(List<Video> videoList) {

        this.videoList = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_gallery_video_item, viewGroup, false);

        this.context = viewGroup.getContext();
        return new VideoViewHolder(rowView, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        Video selectedVideo = videoList.get(position);
        String path = selectedVideo.getIconPath() != null
                ? selectedVideo.getIconPath() : selectedVideo.getMediaPath();
        Glide.with(context)
                .load(path)
                .centerCrop()
                .error(R.drawable.gatito_rules)
                .into(holder.thumb);
        holder.overlay.setSelected(position == selectedVideoPosition);
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

    public void setRecyclerViewClickListener(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public void appendVideos(List<Video> videoList) {
        videoList.addAll(videoList);
    }

    public boolean isVideoListEmpty() {
        return videoList.isEmpty();
    }


    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        RecyclerViewClickListener onClickListener;

        @InjectView(R.id.gallery_thumb)
        ImageView thumb;

        @InjectView(R.id.gallery_overlay)
        ImageView overlay;

        public VideoViewHolder(View itemView, RecyclerViewClickListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedVideoPosition);
                selectedVideoPosition = getPosition();
                notifyItemChanged(selectedVideoPosition);
                onClickListener.onClick(selectedVideoPosition);
            }
            return true;
        }

        @OnClick(R.id.gallery_preview_button)
        public void startVideoPreview(View v){
            String videoPath=videoList.get(getPosition()).getMediaPath();
            //TODO cambiar la actividad de share por la de preview
            Intent i= new Intent(v.getContext(), ShareActivity.class);
            i.putExtra("MEDIA_OUTPUT", videoPath);
            v.getContext().startActivity(i);
        }

    }

}