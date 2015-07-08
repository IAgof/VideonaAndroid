package com.videonasocialmedia.videona.presentation.views.adapter.helper;

/**
 * Created by jca on 7/7/15.
 */
public interface MovableItemsAdapter {

    void moveItem(int fromPositon, int toPosition);

    void finishMovement(int newPosition);
}
