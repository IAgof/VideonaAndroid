package com.videonasocialmedia.videona.presentation.views.adapter.helper;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.videonasocialmedia.videona.presentation.views.activity.GalleryActivity;

/**
 * Created by jca on 7/7/15.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MovableItemsAdapter adapter;
    private Drawable underSwipeIcon;

    public ItemTouchHelperCallback(MovableItemsAdapter adapter, Drawable underSwipeIcon) {
        this.adapter = adapter;
        this.underSwipeIcon=underSwipeIcon;
    }

    public ItemTouchHelperCallback(MovableItemsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.END
                | ItemTouchHelper.START;
        final int swypeFlags = ItemTouchHelper.UP;

        return makeMovementFlags(dragFlags, swypeFlags);
        //return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (underSwipeIcon!=null && actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
            View item= viewHolder.itemView;
            int left= item.getLeft();
            int right= item.getRight();
            int top= item.getTop();
            int bottom= item.getBottom();

            int height= top-bottom;
            int width= right-left;

            //TODO remove hardcoded values
            //underSwipeIcon.setBounds(left+50,top+100,right-50,bottom-5);
            underSwipeIcon.setBounds(left+(width/4),top-height/2,right-width/4,bottom);
            //underSwipeIcon.setBounds(left,top,right,bottom);
            underSwipeIcon.draw(c);
        }
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
        adapter.remove(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //adapter.finishMovement(viewHolder.getAdapterPosition());
    }
}
