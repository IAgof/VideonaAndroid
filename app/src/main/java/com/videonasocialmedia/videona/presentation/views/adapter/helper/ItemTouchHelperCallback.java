package com.videonasocialmedia.videona.presentation.views.adapter.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jca on 7/7/15.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MovableItemsAdapter adapter;

    public ItemTouchHelperCallback(MovableItemsAdapter adapter){
        this.adapter=adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.END
                | ItemTouchHelper.START;
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        boolean result;
        if (source.getItemViewType() != target.getItemViewType()) {
            result = false;
        } else {
            adapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
            result = true;
        }
        return result;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Notify the adapter of the dismissal
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.finishMovement(viewHolder.getAdapterPosition());
    }
}
