/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.effects.Effect;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class MusicCatalogAdapter extends RecyclerView.Adapter<FxItemViewHolder> {

    private Context context;

    private RecyclerClickListener recyclerClickListener;

    private List<Music> elementList;

    public List<Music> getElementList() {
        return elementList;
    }

    public MusicCatalogAdapter(List<Music> elementList) {
        this.elementList = elementList;
    }

    public RecyclerClickListener getRecyclerClickListener() {
        return recyclerClickListener;
    }

    public void setRecyclerClickListener(RecyclerClickListener recyclerClickListener) {
        this.recyclerClickListener = recyclerClickListener;
    }

    @Override
    public FxItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.edit_item_fx, parent, false);
        return new FxItemViewHolder(view, recyclerClickListener);
    }


    @Override
    public void onBindViewHolder(FxItemViewHolder holder, final int position) {
        Music selectedElement = elementList.get(position);
        //TODO coger la url de la imagen del efecto y usar picasso para cachear
        holder.background.setImageResource(selectedElement.getIconResourceId());
        //holder.background.setBackgroundColor(((Music)selectedElement).getColorResourceId());
        holder.background.setBackgroundResource(selectedElement.getColorResourceId());
    }


    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public void appendEffects(List<Effect> effectList) {

        effectList.addAll(effectList);
        notifyDataSetChanged();
    }


}

class FxItemViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

    private RecyclerClickListener onClickListener;

    @InjectView(R.id.edit_item_fx_background)
    ImageView background;

    public FxItemViewHolder(View itemView, RecyclerClickListener onClickListener) {
        super(itemView);
        ButterKnife.inject(this, itemView);

        background.setOnTouchListener(this);
        this.onClickListener = onClickListener;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            onClickListener.onClick(getPosition());

        }

        return true;
    }
}


