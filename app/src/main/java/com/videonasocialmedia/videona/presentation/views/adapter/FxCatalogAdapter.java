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
import com.videonasocialmedia.videona.model.entities.editor.Effect;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * @author Juan Javier Cabanas Abascal
 */
public class FxCatalogAdapter extends RecyclerView.Adapter<FxItemViewHolder> {

    private Context context;

    private List<Effect> effectList;

    public List<Effect> getEffectList() {
        return effectList;
    }

    public FxCatalogAdapter() {
        super();
    }

    public FxCatalogAdapter(List<Effect> effectList) {
        this.effectList = effectList;
    }

    @Override
    public FxItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.edit_item_fx, parent, false);
        return new FxItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(FxItemViewHolder holder, final int position) {
        Effect selectedEffect= effectList.get(position);
        //TODO coger la url de la imagen del efecto y usar picasso para cachear
        holder.background.setImageResource(R.drawable.edit_fragment_sound_icon_audio_normal);
        holder.background.setBackgroundColor(R.color.pastel_palette_green);
    }


    @Override
    public int getItemCount() {
        return effectList.size();
    }

    public void appendEffects(List<Effect> effectList) {

        effectList.addAll(effectList);
        notifyDataSetChanged();
    }


}

class FxItemViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

    @InjectView(R.id.edit_item_fx_background)
    ImageView background;

    public FxItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
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
        return false;

        //TODO implementar funcionalidad

        //La actividad o el fragment deberían escuchar este evento para poder modificar el panel principal
    }


}


