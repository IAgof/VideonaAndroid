package com.videonasocialmedia.videona.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.Filters;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnEffectSelectedListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Veronica Lago Fominaya on 03/11/2015.
 *
 * This class is used to show the camera effects gallery.
 */
public class EffectAdapter
        extends RecyclerView.Adapter<EffectAdapter.cameraEffectViewHolder> {

    private Context context;
    private List<Effect> effects;
    private OnEffectSelectedListener onEffectSelectedListener;
    private int selectedPosition = -1;
    private int previousSelectionPosition = -1;
    private Effect defaultEffect;

    /**
     * Constructor.
     *
     * @param effects the list of the available effects
     */
    public EffectAdapter(List<Effect> effects, OnEffectSelectedListener listener) {
        this.effects = effects;
        this.onEffectSelectedListener = listener;
        defaultEffect = new Effect(null, "AD0", -1, Filters.FILTER_NONE);
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
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_effects_view_holder, viewGroup, false);
        this.context = viewGroup.getContext();
        return new cameraEffectViewHolder(rowView, onEffectSelectedListener);
    }

    @Override
    public void onBindViewHolder(cameraEffectViewHolder holder, int position) {
        Effect selectedEffect = effects.get(position);
        Glide.with(context)
                .load(selectedEffect.getIconResourceId())
                .error(R.drawable.gatito_rules)
                .into(holder.effectImage);
        holder.effectName.setText(selectedEffect.getIconResourceName());
        if (position == selectedPosition) {
            holder.effect.setBackgroundResource(R.color.videona_red_1);
        } else {
            holder.effect.setBackgroundResource(0);
        }
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

    public void resetSelectedEffect() {
        selectedPosition = -1;
        this.notifyDataSetChanged();
    }

    public int getPreviousSelectionPosition() {
        return previousSelectionPosition;
    }

    /**
     * This class is used to controls an item view of cameraEffect and metadata about its place within
     * the recycler view.
     */
    class cameraEffectViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        OnEffectSelectedListener onClickListener;

        @InjectView(R.id.effectViewHolder)
        LinearLayout effect;
        @InjectView(R.id.effectImage)
        ImageView effectImage;
        @InjectView(R.id.effectName)
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
            ButterKnife.inject(this, itemView);
            effect.setOnTouchListener(this);
            this.onClickListener = onClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                previousSelectionPosition = selectedPosition;
                notifyItemChanged(selectedPosition);
                if (selectedPosition == getAdapterPosition()) {
                    resetSelectedEffect();
                    onClickListener.onEffectSelected(defaultEffect);
                } else {
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    onClickListener.onEffectSelected(effects.get(selectedPosition));
                }
            }
            return true;
        }
    }
}
