package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.MovableItemsAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class VideoTimeLineAdapter extends RecyclerView.Adapter<VideoTimeLineAdapter.VideoViewHolder>
        implements MovableItemsAdapter {
    private Context context;
    private VideoTimeLineRecyclerViewClickListener videoTimeLineListener;
    private List<Video> videoList;
    private int selectedVideoPosition = -1;

    public VideoTimeLineAdapter(List<Video> videoList) {
        this.videoList = videoList;
    }

    public VideoTimeLineAdapter(VideoTimeLineRecyclerViewClickListener listener) {
        this.videoList = new ArrayList<>();
        this.setVideoTimeLineListener(listener);
    }

    public void setVideoTimeLineListener(VideoTimeLineRecyclerViewClickListener videoTimeLineListener) {
        this.videoTimeLineListener = videoTimeLineListener;
    }

    @Override
    public void moveItem(int fromPositon, int toPosition) {
        if (fromPositon != toPosition) {
            Collections.swap(videoList, fromPositon, toPosition);
            selectedVideoPosition = toPosition;
            notifyItemMoved(fromPositon, toPosition);
            videoTimeLineListener.onClipMoved(toPosition);
        }
    }

    @Override
    public void remove(int itemPosition) {
        int newPosition = recalculateSelectedVideoPosition(itemPosition);
        videoList.remove(itemPosition);
        notifyItemRemoved(itemPosition);
        updateSelection(newPosition);
    }

    private int recalculateSelectedVideoPosition(int removedItemPosition) {
        int newPosition = selectedVideoPosition;
        if (removedItemPosition < selectedVideoPosition ||
                (removedItemPosition == videoList.size() - 1 &&
                        selectedVideoPosition == removedItemPosition)) {
            newPosition = selectedVideoPosition - 1;
        }
        return newPosition;
    }

    @Override
    public void finishMovement(int newPosition) {
        if (newPosition != -1)
            notifyDataSetChanged();
    }

    public void updateSelection(int positionSelected) {
        if (positionSelected == selectedVideoPosition)
            return;
        notifyItemChanged(selectedVideoPosition);
        selectedVideoPosition = positionSelected;
        notifyItemChanged(selectedVideoPosition);
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
        updateSelection(0);
    }

    public int getSelectedVideoPosition() {
        return selectedVideoPosition;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.edit_videotimeline_video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video current = videoList.get(position);
        drawVideoThumbnail(holder.thumb, current);
        holder.thumb.setSelected(position == selectedVideoPosition);
        holder.thumbOrder.setText(String.valueOf(position + 1));
        if(position == selectedVideoPosition) {
            holder.showDeleteIcon();
        } else {
            holder.hideDeleteIcon();
        }

    }

    public void drawVideoThumbnail(ImageView thumbnailView, Video current) {
        int microSecond = current.getFileStartTime()*1000;
        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
                new VideoBitmapDecoder(microSecond),
                bitmapPool,
                DecodeFormat.PREFER_ARGB_8888);

        String path = current.getIconPath() != null
                ? current.getIconPath() : current.getMediaPath();
        Glide.with(context)
                .load(path)
                .asBitmap()
                .videoDecoder(decoder)
                .centerCrop()
                .error(R.drawable.fragment_gallery_no_image)
                .into(thumbnailView);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public Video getItem(int position) {
        return videoList.get(position);
    }


    /**
     * Video ViewHolder class
      */
    class VideoViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @Bind(R.id.timeline_video_thumb)
        ImageView thumb;
        @Bind(R.id.text_clip_order)
        TextView thumbOrder;
        @Bind(R.id.image_remove_video)
        ImageView removeVideo;


        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.timeline_video_thumb)
        public void onClipClick() {
            int position = getAdapterPosition();
            showDeleteIcon();
            updateSelection(position);
            videoTimeLineListener.onClipClicked(position);
        }

        public void showDeleteIcon() {
            removeVideo.setVisibility(View.VISIBLE);
        }

        public void hideDeleteIcon() {
            removeVideo.setVisibility(View.GONE);
        }

//        @OnLongClick(R.id.timeline_video_thumb)
//        public boolean videoOnLongClick() {
//            thumb.setRotation(20);
//            videoTimeLineListener.onClipLongClicked();
//            return true;
//        }

        @OnClick (R.id.image_remove_video)
        public void onClickRemoveVideoTimeline() {
            videoTimeLineListener.onClipRemoveClicked(getAdapterPosition());
        }

        @Override
        public void onItemSelected() {
            thumb.setRotation(20);
        }

        @Override
        public void onItemClear() {
            thumb.setRotation(0);
        }
    }
}
