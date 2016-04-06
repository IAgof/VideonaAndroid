/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is used to show the music gallery.
 */
public class MusicGalleryAdapter extends RecyclerView.Adapter<MusicGalleryAdapter.MusicViewHolder> {

    private Context context;
    private List<Music> musicList;
    private MusicRecyclerViewClickListener musicRecyclerViewClickListener;
    private int selectedMusicPosition = -1;

    /**
     * Constructor.
     *
     * @param musicList the list of the available songs
     */
    public MusicGalleryAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    /**
     * This method returns the list of the available songs
     *
     * @return
     */
    public List<Music> getElementList() {
        return musicList;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_gallery_music_item, viewGroup, false);
        this.context = viewGroup.getContext();
        return new MusicViewHolder(rowView, musicRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        Music selectedMusic = musicList.get(position);
        holder.thumb.setBackgroundResource(selectedMusic.getColorResourceId());
        Glide.with(context)
                .load(selectedMusic.getIconResourceId())
                .error(R.drawable.gatito_rules)
                .into(holder.thumb);
        holder.overlay.setSelected(position == selectedMusicPosition);
        holder.overlayIcon.setSelected(position == selectedMusicPosition);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    /**
     * Returns the music for a given position
     *
     * @param position the position of the music element
     * @return
     */
    public Music getMusic(int position) {
        return musicList.get(position);
    }

    /**
     * Sets the listener of the recycler view
     *
     * @param musicRecyclerViewClickListener
     */
    public void setMusicRecyclerViewClickListener(MusicRecyclerViewClickListener musicRecyclerViewClickListener) {
        this.musicRecyclerViewClickListener = musicRecyclerViewClickListener;
    }

    /**
     * Appends new music to the actual music list
     *
     * @param musicList
     */
    public void appendMusic(List<Music> musicList) {
        this.musicList.addAll(musicList);
    }

    /**
     * Checks if the music list is empty
     *
     * @return
     */
    public boolean isMusicListEmpty() {
        return musicList.isEmpty();
    }

    /**
     * This class is used to controls an item view of music and metadata about its place within
     * the recycler view.
     */
    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        MusicRecyclerViewClickListener onClickListener;

        @Bind(R.id.gallery_thumb)
        ImageView thumb;

        @Bind(R.id.gallery_overlay)
        RelativeLayout overlay;

        @Bind(R.id.gallery_overlay_icon)
        ImageView overlayIcon;

        /**
         * Constructor.
         *
         * @param itemView        the view of the music elements
         * @param onClickListener the listener which controls the actions over the music elements
         */
        public MusicViewHolder(View itemView, MusicRecyclerViewClickListener onClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedMusicPosition);
                selectedMusicPosition = getAdapterPosition();
                notifyItemChanged(selectedMusicPosition);
                onClickListener.onClick(selectedMusicPosition);
            }
            return true;
        }
    }
}