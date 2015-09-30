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

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnFxSelectedListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show the camera fx gallery.
 */
public class CameraEffectsAdapter extends
        RecyclerView.Adapter<CameraEffectsAdapter.CameraEffectViewHolder> {

    private Context context;
    private List<CameraEffectFx> cameraFxList;
    private OnFxSelectedListener onFxSelectedListener;
    private int selectedPosition = 0;

    /**
     * Constructor.
     *
     * @param cameraFxList the list of the available songs
     */
    public CameraEffectsAdapter(List<CameraEffectFx> cameraFxList, OnFxSelectedListener listener) {
        this.cameraFxList = cameraFxList;
        this.onFxSelectedListener = listener;
    }

    /**
     * Creates an adapter with the default list of effects
     */
    public CameraEffectsAdapter(OnFxSelectedListener listener) {
        this.cameraFxList = CameraEffectFx.getCameraEffectList();
        this.onFxSelectedListener = listener;
    }

    /**
     * This method returns the list of the available songs
     *
     * @return
     */
    public List<CameraEffectFx> getElementList() {
        return cameraFxList;
    }

    @Override
    public CameraEffectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_item_camera_effect_fx, viewGroup, false);
        this.context = viewGroup.getContext();
        return new CameraEffectViewHolder(rowView, onFxSelectedListener);
    }

    @Override
    public void onBindViewHolder(CameraEffectViewHolder holder, int position) {
        CameraEffectFx selectedCameraFx = cameraFxList.get(position);

        if (position == selectedPosition) {
            Glide.with(context)
                    .load(selectedCameraFx.getIconPressedResourceId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.thumb);
        } else {
            Glide.with(context)
                    .load(selectedCameraFx.getIconResourceId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.thumb);
        }
    }

    @Override
    public int getItemCount() {
        return cameraFxList.size();
    }

    /**
     * Returns the cameraEffectFx for a given position
     *
     * @param position the position of the cameraEffectFx element
     * @return
     */
    public CameraEffectFx getCameraEffectFx(int position) {
        return cameraFxList.get(position);
    }

    /**
     * Sets the listener of the recycler view
     *
     * @param cameraEffectFxRecyclerViewClickListener
     */
    public void setClickListener
    (OnFxSelectedListener cameraEffectFxRecyclerViewClickListener) {
        this.onFxSelectedListener = cameraEffectFxRecyclerViewClickListener;
    }

    /**
     * Appends new cameraEffectFx to the actual cameraEffectFx list
     *
     * @param cameraFxListList
     */
    public void appendCameraEffectFx(List<CameraEffectFx> cameraFxListList) {
        this.cameraFxList.addAll(cameraFxListList);
    }

    /**
     * Checks if the cameraEffectFx list is empty
     *
     * @return
     */
    public boolean isCameraEffectFxListEmpty() {
        return cameraFxList.isEmpty();
    }

    public void resetSelectedEffect() {
        selectedPosition=-1;
        this.notifyDataSetChanged();
    }

    /**
     * This class is used to controls an item view of cameraEffectFx and metadata about its place
     * within the recycler view.
     */
    class CameraEffectViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        OnFxSelectedListener onClickListener;

        @InjectView(R.id.imageCameraEffectFx)
        ImageView thumb;

        /**
         * Constructor.
         *
         * @param itemView        the view of the cameraEffectFx elements
         * @param onClickListener the listener which controls the actions over the cameraEffectFx
         *                        elements
         */
        public CameraEffectViewHolder(View itemView,
                                      OnFxSelectedListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
                onClickListener.onFxSelected(cameraFxList.get(selectedPosition));
            }
            return true;
        }
    }
}