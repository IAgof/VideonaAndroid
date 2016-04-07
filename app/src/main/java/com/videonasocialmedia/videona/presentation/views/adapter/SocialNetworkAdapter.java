package com.videonasocialmedia.videona.presentation.views.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jca on 14/12/15.
 */
public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.SocialNetworkViewHolder> {

    private List<SocialNetwork> socialNetworks;
    private OnSocialNetworkClickedListener listener;

    public SocialNetworkAdapter(List<SocialNetwork> socialNetworks, OnSocialNetworkClickedListener listener) {
        this.socialNetworks = socialNetworks;
        this.listener = listener;
        notifyDataSetChanged();
    }

    public SocialNetworkAdapter(OnSocialNetworkClickedListener listener) {
        this.listener = listener;
        socialNetworks = new ArrayList<>();
    }

    @Override
    public SocialNetworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_network_viewholder, parent, false);
        return new SocialNetworkViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(SocialNetworkViewHolder holder, int position) {
        SocialNetwork current = socialNetworks.get(position);
        holder.icon.setImageDrawable(current.getIcon());
        if (holder.name != null)
            holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return socialNetworks.size();
    }

    public void setSocialNetworkList(List<SocialNetwork> socialNetworkList) {
        this.socialNetworks = socialNetworkList;
    }

    public interface OnSocialNetworkClickedListener {
        void onSocialNetworkClicked(SocialNetwork socialNetwork);
    }

    class SocialNetworkViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon)
        ImageView icon;
        @Nullable
        @Bind(R.id.name)
        TextView name;
        @Nullable
        @Bind(R.id.checkbox)
        CheckBox checkBox;

        public SocialNetworkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.onSocialNetworkClicked(socialNetworks.get(position));
                }
            });
        }
    }
}
