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
import com.videonasocialmedia.videona.effects.repository.model.Effect;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * This class is used to show the camera effects gallery.
 */
public class EffectAdapter
        extends RecyclerView.Adapter<EffectAdapter.cameraEffectViewHolder> {
    private static final int GIFT_VIEW_TYPE = 300;
    private static final int EFFECT_VIEW_TYPE = 254;
    private RealmResults<Effect> realmEffects;

    private Context context;
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

    public EffectAdapter(RealmResults<Effect> effects, OnEffectSelectedListener listener) {
        this.realmEffects = effects;
        this.onEffectSelectedListener = listener;
    }

    /**
     * This method returns the list of the available effects
     *
     * @return
     */

    public RealmResults<Effect> getRealmElementList() {
        return realmEffects.where().findAll();
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
        //Effect selectedEffect = effects.get(position);
        Effect selectedEffect = realmEffects.get(position);
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

        return realmEffects.get(position).getActivated()
                ? EFFECT_VIEW_TYPE : GIFT_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        //return effects.size();
        return realmEffects.size();
    }

    /**
     * Returns the effect for a given position
     *
     * @param position the position of the effect element
     * @return
     */
    public Effect getEffect(int position) {
        //return effects.get(position);
        return realmEffects.get(position);
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


    public void setRealmEffectList(RealmResults<Effect> effectList) {
        this.realmEffects = effectList;
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
                    onClickListener.onEffectSelectionCancel(realmEffects.get(adapterPosition));
                } else {
                    effectSelected = true;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    onClickListener.onEffectSelected(realmEffects.get(selectedPosition));
                }
            }
            return true;
        }

    }
}