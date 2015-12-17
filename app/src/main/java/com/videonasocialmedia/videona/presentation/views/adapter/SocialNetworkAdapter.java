package com.videonasocialmedia.videona.presentation.views.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.social.SocialNetworkApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by jca on 14/12/15.
 */
public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.SocialNetworkViewHolder> {

    private List<SocialNetworkApp> socialNetworks;
    private OnSocialNetworkClickedListener listener;

    public SocialNetworkAdapter(List<SocialNetworkApp> socialNetworks, OnSocialNetworkClickedListener listener) {
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
        SocialNetworkApp current = socialNetworks.get(position);
        holder.icon.setImageDrawable(current.getIcon());
        if (holder.name != null)
            holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return socialNetworks.size();
    }

    public void setSocialNetworkList(List<SocialNetworkApp> socialNetworkList) {
        this.socialNetworks = socialNetworkList;
    }

    public interface OnSocialNetworkClickedListener {
        void onSocialNetworkClicked(SocialNetworkApp socialNetworkApp);
    }

    class SocialNetworkViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.icon)
        ImageView icon;
        @Optional
        @InjectView(R.id.name)
        TextView name;
        @Optional
        @InjectView(R.id.checkbox)
        CheckBox checkBox;

        public SocialNetworkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
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
