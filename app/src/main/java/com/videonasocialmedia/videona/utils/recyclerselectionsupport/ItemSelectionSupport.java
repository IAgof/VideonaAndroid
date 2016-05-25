package com.videonasocialmedia.videona.utils.recyclerselectionsupport;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;

import com.videonasocialmedia.videona.R;

/**
 * Created by jca on 30/6/15.
 */

public class ItemSelectionSupport {
    private final RecyclerView mRecyclerView;
    private final TouchListener mTouchListener;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private ItemSelectionSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        mTouchListener = new TouchListener(recyclerView);
        recyclerView.addOnItemTouchListener(mTouchListener);
    }

    public static ItemSelectionSupport addTo(RecyclerView recyclerView) {
        ItemSelectionSupport itemSelectionSupport = from(recyclerView);
        if (itemSelectionSupport == null) {
            itemSelectionSupport = new ItemSelectionSupport(recyclerView);
            recyclerView.setTag(R.id.twowayview_item_click_support, itemSelectionSupport);
        } else {
            // TODO: Log warning
        }

        return itemSelectionSupport;
    }

    public static ItemSelectionSupport from(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return null;
        }

        return (ItemSelectionSupport) recyclerView.getTag(R.id.twowayview_item_click_support);
    }

    public static void removeFrom(RecyclerView recyclerView) {
        final ItemSelectionSupport itemSelectionSupport = from(recyclerView);
        if (itemSelectionSupport == null) {
            // TODO: Log warning
            return;
        }

        recyclerView.removeOnItemTouchListener(itemSelectionSupport.mTouchListener);
        recyclerView.setTag(R.id.twowayview_item_click_support, null);
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!mRecyclerView.isLongClickable()) {
            mRecyclerView.setLongClickable(true);
        }

        mItemLongClickListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked.
         *
         * @param parent   The RecyclerView where the click happened.
         * @param view     The view within the RecyclerView that was clicked
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked and held.
         *
         * @param parent   The RecyclerView where the click happened
         * @param view     The view within the RecyclerView that was clicked
         * @param position The position of the view in the list
         * @param id       The row id of the item that was clicked
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }

    private class TouchListener extends ClickItemTouchListener {
        TouchListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        boolean performItemClick(RecyclerView parent, View view, int position, long id) {
            if (mItemClickListener != null) {
                view.playSoundEffect(SoundEffectConstants.CLICK);
                mItemClickListener.onItemClick(parent, view, position, id);
                return true;
            }

            return false;
        }

        @Override
        boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
            if (mItemLongClickListener != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return mItemLongClickListener.onItemLongClick(parent, view, position, id);
            }

            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }
}
