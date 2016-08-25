package com.videonasocialmedia.videona.presentation.views.adapter.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jca on 7/7/15.
 */
public class VideoTimeLineTouchHelperCallback extends ItemTouchHelper.Callback {

    private MovableItemsAdapter adapter;

    public VideoTimeLineTouchHelperCallback(MovableItemsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.END
                | ItemTouchHelper.START;

        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        adapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        adapter.finishMovement(viewHolder.getAdapterPosition());
//    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {
        // We only want the active item
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ItemTouchHelperViewHolder itemViewHolder =
                        (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemViewHolder =
                    (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
        adapter.finishMovement(viewHolder.getAdapterPosition());
    }
}
