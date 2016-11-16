/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.effects.domain.model.EffectType;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is used to show the camera effects gallery.
 */
public class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.cameraEffectViewHolder> {
    private static final int COVERED_VIEW_TYPE = 300;
    private static final int EFFECT_VIEW_TYPE = 254;

    private List<Effect> listEffects;

    private Context context;
    private OnEffectSelectedListener onEffectSelectedListener;
    private int selectedPosition = -1;
    private int previousSelectionPosition = -1;
    private boolean effectSelected = false;

    /**
     * Constructor.
     *
     * @param effects the list of the available effects
     */

    public EffectAdapter(List<Effect> effects, OnEffectSelectedListener listener) {
        this.listEffects = effects;
        this.onEffectSelectedListener = listener;
    }

    /**
     * This method returns the list of the available effects
     *
     * @return
     */

    public List<Effect> getListEffectsElementList() {

        return listEffects;
    }

    @Override
    public cameraEffectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        this.context = viewGroup.getContext();
        View rowView;
        switch (viewType) {
            case COVERED_VIEW_TYPE:
                rowView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.record_effects_gift_view_holder, viewGroup, false);
                break;
            case EFFECT_VIEW_TYPE:
            default:
                rowView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.record_effects_view_holder, viewGroup, false);

                break;
        }
        return new cameraEffectViewHolder(rowView, onEffectSelectedListener);
    }

    @Override
    public void onBindViewHolder(cameraEffectViewHolder holder, int position) {

        Effect selectedEffect = listEffects.get(position);

        if(selectedEffect.getEffectType().compareTo(EffectType.SHADER.name()) == 0){
            Glide.with(context)
                .load(selectedEffect.getIconId())
                .error(R.drawable.gatito_rules)
                .into(holder.effectImage);

            if (holder.effectName != null) {
                holder.effectName.setText(selectedEffect.getName());
            }
            if (position == selectedPosition) {
                holder.effect.setBackgroundResource(R.color.colorAccent);
            } else {
                holder.effect.setBackgroundResource(0);
            }

            return;
        }


        if ((getItemViewType(position) != COVERED_VIEW_TYPE)) {
            Glide.with(context)
                    .load(selectedEffect.getIconId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.effectImage);

            if (holder.effectName != null) {
                holder.effectName.setText(selectedEffect.getName());
            }
            if (position == selectedPosition) {
                holder.effect.setBackgroundResource(R.color.colorAccent);
            } else {
                holder.effect.setBackgroundResource(0);
            }
        } else {

            Glide.with(context)
                    .load(selectedEffect.getCoverIconId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.effectImage);

        }
    }

    @Override
    public int getItemViewType(int position) {

        return listEffects.get(position).getActivated() ? EFFECT_VIEW_TYPE : COVERED_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return listEffects.size();
    }

    /**
     * Returns the effect for a given position
     *
     * @param position the position of the effect element
     * @return
     */
    public Effect getEffect(int position) {
        return listEffects.get(position);
    }


    /**
     * Sets the listener of the recycler view
     *
     * @param onEffectSelectedListener
     */
    public void setClickListener(OnEffectSelectedListener
                                         onEffectSelectedListener) {
        this.onEffectSelectedListener = onEffectSelectedListener;
    }


    /**
     * Checks if some effect has been selected
     *
     * @return
     */
    public boolean isEffectSelected() {
        return effectSelected;
    }

    public void resetSelectedEffect() {
        selectedPosition = -1;
        effectSelected = false;
        this.notifyDataSetChanged();
    }

    public int getPreviousSelectionPosition() {
        return previousSelectionPosition;
    }

    public int getSelectionPosition() {
        if (selectedPosition == -1) {
            return 0;
        }
        return selectedPosition;
    }


    public void setEffectList(List<Effect> effectList) {
        this.listEffects = effectList;
        notifyDataSetChanged();
    }

    /**
     * This class is used to controls an item view of cameraEffect and metadata about its place within
     * the recycler view.
     */
    class cameraEffectViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        OnEffectSelectedListener onClickListener;

        @Bind(R.id.effectViewHolder)
        LinearLayout effect;
        @Bind(R.id.effectImage)
        ImageView effectImage;
        @Nullable
        @Bind(R.id.effectName)
        TextView effectName;

        /**
         * Constructor.
         *
         * @param itemView        the view of the cameraEffect elements
         * @param onClickListener the listener which controls the actions over the cameraEffect elements
         */
        public cameraEffectViewHolder(View itemView,
                                      OnEffectSelectedListener onClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            effect.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                previousSelectionPosition = selectedPosition;
                notifyItemChanged(selectedPosition);
                if (selectedPosition == getAdapterPosition()) {
                    int adapterPosition = getAdapterPosition();
                    resetSelectedEffect();
                    onClickListener.onEffectSelectionCancel(listEffects.get(adapterPosition));
                } else {
                    effectSelected = true;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    onClickListener.onEffectSelected(listEffects.get(selectedPosition));
                }
            }
            return true;
        }

    }
}