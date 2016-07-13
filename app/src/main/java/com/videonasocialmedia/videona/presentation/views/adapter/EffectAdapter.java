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
import com.videonasocialmedia.videona.auth.domain.model.PermissionType;
import com.videonasocialmedia.videona.auth.domain.usecase.LoginUser;
import com.videonasocialmedia.videona.effects.domain.model.Effect;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is used to show the camera effects gallery.
 */
public class EffectAdapter
        extends RecyclerView.Adapter<EffectAdapter.cameraEffectViewHolder> {
    private static final int GIFT_VIEW_TYPE = 300;
    private static final int EFFECT_VIEW_TYPE = 254;

    private Context context;
    private List<Effect> effects;
    private OnEffectSelectedListener onEffectSelectedListener;
    private int selectedPosition = -1;
    private int previousSelectionPosition = -1;
    private boolean effectSelected = false;
    private LoginUser loginUser = new LoginUser();


    /**
     * Constructor.
     *
     * @param effects the list of the available effects
     */
    public EffectAdapter(List<Effect> effects, OnEffectSelectedListener listener) {
        this.effects = effects;
        this.onEffectSelectedListener = listener;
    }

    /**
     * This method returns the list of the available effects
     *
     * @return
     */
    public List<Effect> getElementList() {
        return effects;
    }

    @Override
    public cameraEffectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        this.context = viewGroup.getContext();
        View rowView;
        switch (viewType) {
            case GIFT_VIEW_TYPE:
                rowView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.record_gift_effects_view_holder, viewGroup, false);
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
        Effect selectedEffect = effects.get(position);
        if (getItemViewType(position) != GIFT_VIEW_TYPE) {
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
        }
    }

    @Override
    public int getItemViewType(int position) {
        return effects.get(position).getPermissionType() ==
                PermissionType.LOGGED_IN && !loginUser.userIsLoggedIn()
                ? GIFT_VIEW_TYPE : EFFECT_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return effects.size();
    }

    /**
     * Returns the effect for a given position
     *
     * @param position the position of the effect element
     * @return
     */
    public Effect getEffect(int position) {
        return effects.get(position);
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
     * Appends new effect to the actual effect list
     *
     * @param effects
     */
    public void appendCameraEffect(List<Effect> effects) {
        this.effects.addAll(effects);
    }

    /**
     * Checks if the cameraEffectColor list is empty
     *
     * @return
     */
    public boolean isCameraEffectColorListEmpty() {
        return effects.isEmpty();
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
        this.effects = effectList;
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
                    onClickListener.onEffectSelectionCancel(effects.get(adapterPosition));
                } else {
                    effectSelected = true;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    onClickListener.onEffectSelected(effects.get(selectedPosition));
                }
            }
            return true;
        }

    }
}