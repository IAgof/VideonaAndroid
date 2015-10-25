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
import com.videonasocialmedia.videona.presentation.views.listener.OnColorEffectSelectedListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This class is used to show the camera color gallery.
 */
public class CameraColorFilterAdapter
        extends RecyclerView.Adapter<CameraColorFilterAdapter.cameraEffectViewHolder> {

    private Context context;
    private List<CameraEffectColor> cameraEffectColors;
    private OnColorEffectSelectedListener onColorEffectSelectedListener;
    private int selectedPosition = -1;
    private int previousSelectionPosition=-1;

    /**
     * Constructor.
     *
     * @param cameraEffectColor the list of the available color effects
     */
    public CameraColorFilterAdapter(List<CameraEffectColor> cameraEffectColor,
                                    OnColorEffectSelectedListener listener) {
        this.cameraEffectColors = cameraEffectColor;
        this.onColorEffectSelectedListener = listener;
    }

    /**
     * Construct an adapter with a defaultList
     */
    public CameraColorFilterAdapter(OnColorEffectSelectedListener listener) {
        cameraEffectColors = CameraEffectColor.getDefaultCameraEffectColorList();
        this.onColorEffectSelectedListener = listener;
    }

    /**
     * This method returns the list of the available color effects
     *
     * @return
     */
    public List<CameraEffectColor> getElementList() {
        return cameraEffectColors;
    }

    @Override
    public cameraEffectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_item_camera_effect_color, viewGroup, false);
        this.context = viewGroup.getContext();
        return new cameraEffectViewHolder(rowView, onColorEffectSelectedListener);
    }

    @Override
    public void onBindViewHolder(cameraEffectViewHolder holder, int position) {
        CameraEffectColor selectedCameraEffectColor = cameraEffectColors.get(position);
        if (position == selectedPosition) {
            Glide.with(context)
                    .load(selectedCameraEffectColor.getIconPressedResourceId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.thumb);
        } else {
            Glide.with(context)
                    .load(selectedCameraEffectColor.getIconResourceId())
                    .error(R.drawable.gatito_rules)
                    .into(holder.thumb);
        }
    }

    @Override
    public int getItemCount() {
        return cameraEffectColors.size();
    }

    /**
     * Returns the cameraEffectColor for a given position
     *
     * @param position the position of the cameraEffectColor element
     * @return
     */
    public CameraEffectColor getCameraEffectColor(int position) {
        return cameraEffectColors.get(position);
    }

    /**
     * Sets the listener of the recycler view
     *
     * @param onColorEffectSelectedListener
     */
    public void setClickListener(OnColorEffectSelectedListener
                                         onColorEffectSelectedListener) {
        this.onColorEffectSelectedListener = onColorEffectSelectedListener;
    }

    /**
     * Appends new cameraEffectColor to the actual cameraEffectColor list
     *
     * @param cameraEffectColor
     */
    public void appendCameraEffectColor(List<CameraEffectColor> cameraEffectColor) {
        this.cameraEffectColors.addAll(cameraEffectColor);
    }

    /**
     * Checks if the cameraEffectColor list is empty
     *
     * @return
     */
    public boolean isCameraEffectColorListEmpty() {
        return cameraEffectColors.isEmpty();
    }

    public void resetSelectedEffect() {
        selectedPosition = -1;
        this.notifyDataSetChanged();
    }

    public int getPreviousSelectionPosition() {
        return previousSelectionPosition;
    }

    /**
     * This class is used to controls an item view of cameraEffectColor and metadata about its place within
     * the recycler view.
     */
    class cameraEffectViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        OnColorEffectSelectedListener onClickListener;

        @InjectView(R.id.imageCameraEffectColor)
        ImageView thumb;

        /**
         * Constructor.
         *
         * @param itemView        the view of the cameraEffectColor elements
         * @param onClickListener the listener which controls the actions over the cameraEffectColor elements
         */
        public cameraEffectViewHolder(View itemView,
                                      OnColorEffectSelectedListener onClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            thumb.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                previousSelectionPosition=selectedPosition;
                notifyItemChanged(selectedPosition);
                //if (getAdapterPosition()==selectedPosition)
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
                onClickListener.onColorEffectSelected(cameraEffectColors.get(selectedPosition));
            }
            return true;
        }
    }
}