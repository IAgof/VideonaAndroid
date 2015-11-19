/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.listener;

public interface OnSelectionModeListener {

    void onNoItemSelected();

    void onItemChecked();

    void onItemUnchecked();

    void onExitSelection();

    void onConfirmSelection();
}
