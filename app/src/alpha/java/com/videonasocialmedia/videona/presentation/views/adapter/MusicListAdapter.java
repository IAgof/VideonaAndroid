package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.views.listener.MusicRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListItemViewHolder> {

    Context context;
    List<Music> musicList;
    MusicRecyclerViewClickListener clickListener;

    public void setMusicRecyclerViewClickListener(MusicRecyclerViewClickListener
                                                          MusicRecyclerViewClickListener) {
        clickListener = MusicRecyclerViewClickListener;
    }

    @Override
    public MusicListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_list_item_view_holder, viewGroup, false);
        this.context = viewGroup.getContext();
        return new MusicListItemViewHolder(rowView, musicList);
    }

    @Override
    public void onBindViewHolder(MusicListItemViewHolder holder, int position) {
        Music music = musicList.get(position);

        Glide.with(context)
                .load(music.getIconResourceId())
                .error(R.drawable.gatito_rules)
                .into(holder.musicImage);
        holder.musicTitle.setText(music.getMusicTitle());
    }

    @Override
    public int getItemCount() {
        int result = 0;
        if (musicList != null)
            result = musicList.size();
        return result;
    }


    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    class MusicListItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.music_title)
        TextView musicTitle;
        @Bind(R.id.music_image)
        ImageView musicImage;

        //        private MusicRecyclerViewClickListener clickListener;
        private List<Music> musicList;

        public MusicListItemViewHolder(View itemView, List<Music> musicList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //this.clickListener = clickListener;
            this.musicList = musicList;

        }

        @OnClick({R.id.music_title, R.id.music_image})
        public void onClick() {
            Music music = musicList.get(getAdapterPosition());
            clickListener.onClick(music);
        }
    }
}



