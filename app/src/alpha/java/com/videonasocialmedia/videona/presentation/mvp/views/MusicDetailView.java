package com.videonasocialmedia.videona.presentation.mvp.views;

/**
 *
 */
public interface MusicDetailView {
    void showAuthor(String author);

    void showTitle(String title);

    void showImage(String imagePath);

    void showImage(int iconResourceId);

    void setupScene(boolean isMusicInProject);

    void showBackground(int colorResourceId);
}
