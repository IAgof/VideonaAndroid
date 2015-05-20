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

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * This class is used to show the music gallery.
 */
public class MusicGalleryAdapter extends RecyclerView.Adapter<MusicGalleryAdapter.MusicViewHolder> {

    private Context context;
    private List<Music> musicList;
    private RecyclerViewClickListener recyclerViewClickListener;

    private int selectedMusicPosition = -1;

    public MusicGalleryAdapter(List<Music> musicList) {

        this.musicList = musicList;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_gallery_video_item, viewGroup, false);

        this.context = viewGroup.getContext();
        return new MusicViewHolder(rowView, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {

        Music selectedMusic = musicList.get(position);
        String path = selectedMusic.getIconPath() != null
                ? selectedMusic.getIconPath() : selectedMusic.getMediaPath();
        Glide.with(context)
                .load(path)
                .centerCrop()
                .error(R.drawable.gatito_rules)
                .into(holder.thumb);
        holder.overlay.setSelected(position == selectedMusicPosition);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

//    public List<Video> getVideoList() {
//        return videoList;
//    }

    public Music getMusic(int position) {
        return musicList.get(position);
    }

    public void setRecyclerViewClickListener(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public void appendMusic(List<Music> musicList) {
        musicList.addAll(musicList);
    }

    public boolean isMusicListEmpty() {
        return musicList.isEmpty();
    }


    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        RecyclerViewClickListener onClickListener;

        @InjectView(R.id.gallery_thumb)
        ImageView thumb;

        @InjectView(R.id.gallery_overlay)
        ImageView overlay;

        public MusicViewHolder(View itemView, RecyclerViewClickListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedMusicPosition);
                selectedMusicPosition = getPosition();
                notifyItemChanged(selectedMusicPosition);
                onClickListener.onClick(selectedMusicPosition);
            }
            return true;
        }

        //
        @OnClick(R.id.gallery_preview_button)
        public void updateMusicSelected(View v){
            String musicPath=musicList.get(getPosition()).getMediaPath();
            //TODO mirar si le digo aquí a la actividad que se ha pulsado otra o lo hago en el onTouch
        }

    }

}