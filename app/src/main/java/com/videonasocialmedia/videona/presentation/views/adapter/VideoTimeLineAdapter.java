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
import com.videonasocialmedia.videona.eventbus.events.PreviewingVideoChangedEvent;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.views.adapter.helper.MovableItemsAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas Abascal
 */
public class VideoTimeLineAdapter extends RecyclerView.Adapter<VideoTimeLineAdapter.VideoViewHolder>
        implements MovableItemsAdapter {

    private final int TYPE_VIDEO = 0;
    private final int TYPE_ADD_BUTTON = 1;

    private Context context;
    private List<Video> videoList;
    private List<Object> timeLine;
    private VideoTimeLineRecyclerViewClickListener clickListener;

    private int selectedVideoPosition = -1;

    public VideoTimeLineAdapter(List<Video> videoList) {
        this.videoList = videoList;
    }

    public VideoTimeLineAdapter() {
        this.videoList = new ArrayList<>();
    }

    public void setClickListener(VideoTimeLineRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void moveItem(int fromPositon, int toPosition) {
        if (fromPositon != toPosition) {
            Collections.swap(videoList, fromPositon, toPosition);
            selectedVideoPosition = toPosition;
            notifyItemMoved(fromPositon, toPosition);
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
        EventBus.getDefault().post(new PreviewingVideoChangedEvent(selectedVideoPosition, true));
    }

    private void updateSelection(int positionSelected) {
        notifyItemChanged(selectedVideoPosition);
        selectedVideoPosition = positionSelected;
        notifyItemChanged(selectedVideoPosition);
        EventBus.getDefault().post(new PreviewingVideoChangedEvent(selectedVideoPosition, true));
    }

    public void onEvent (PreviewingVideoChangedEvent event){
        if (!event.fromUser){
            updateSelection(event.previewingVideoIndex);
        }
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
        updateSelection(0);
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
        Video current = videoList.get(position);
        String path = current.getIconPath() != null
                ? current.getIconPath() : current.getMediaPath();
        Glide.with(context)
                .load(path)
                .centerCrop()
                .error(R.drawable.fragment_gallery_no_image)
                .into(holder.thumb);
        holder.thumb.setSelected(position == selectedVideoPosition);
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

        @OnClick(R.id.timelinevideo_thumb)
        public void videoClick() {
            updateSelection(getAdapterPosition());
            clickListener.onVideoClicked(getAdapterPosition());
        }
    }
}
