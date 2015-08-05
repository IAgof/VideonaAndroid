package com.videonasocialmedia.videona.presentation.views.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryMastersPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnSlideListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGallerySlideFragment extends VideoGalleryFragment {

    @InjectView(R.id.button_slide_bottom_panel)
    ImageButton slideButton;

    GalleryMastersPresenter galleryMastersPresenter;
    OnSlideListener onSlideListener;
    RecyclerView.LayoutManager layoutManager;

    public enum Direction {UP, DOWN}

    public static VideoGallerySlideFragment newInstance(int folder, int selectionMode) {
        VideoGallerySlideFragment videoGalleryFragment = new VideoGallerySlideFragment();
        Bundle args = new Bundle();
        args.putInt("FOLDER", folder);
        args.putInt("SELECTION_MODE", selectionMode);
        videoGalleryFragment.setArguments(args);
        return videoGalleryFragment;
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        onSelectionModeListener = (OnSelectionModeListener) a;
        onSlideListener = (OnSlideListener) a;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_fragment_slide_catalog, container, false);
        ButterKnife.inject(this, v);
        if (videoGalleryPresenter == null)
            videoGalleryPresenter = new VideoGalleryPresenter(this);
        if (galleryMastersPresenter == null)
            galleryMastersPresenter = new GalleryMastersPresenter(this);
        layoutManager = new GridLayoutManager(this.getActivity(), 6,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        timeChangesHandler.removeCallbacksAndMessages(null);
        onSelectionModeListener.onExitSelection();
    }

    public void loadVideoListToProject(List<Video> videoList) {
        galleryMastersPresenter.loadVideoListToProject(videoList);
    }

    @Override
    public void showVideoTimeline() {
        onSelectionModeListener.onConfirmSelection();
    }

    @OnClick(R.id.button_slide_bottom_panel)
    public void onClick(View view) {
        onSlideListener.onSlide();
    }

    public void slideUp(ViewGroup centralPanelView, ViewGroup bottomPanelView, int centralPanelHeight, int originalEditBottomPanelHeight, Direction direction) {
        //if (view != null) {

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        bottomPanelView.getGlobalVisibleRect(startBounds);
        centralPanelView.getGlobalVisibleRect(finalBounds);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(bottomPanelView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(bottomPanelView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(bottomPanelView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(bottomPanelView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(3000);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //mCurrentAnimator = null;
            }
        });
        set.start();
        //mCurrentAnimator = set;



        /*
            int height = calculateTranslation(centralPanelHeight, originalEditBottomPanelHeight);
            int translateY = direction == Direction.UP ? -height : height;
            runSlideUpAnimation(view, translateY, centralPanelHeight, new AccelerateInterpolator(3));
            */
        //}
    }

    /**
     * Takes height + margins
     * @return translation in pixels
     */
    private int calculateTranslation(int centralPanelHeight, int originalEditBottomPanelHeight) {
        /*
        int height = view.getHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        //int margins = params.topMargin + params.bottomMargin;
        int margins = 0;
        */

        return centralPanelHeight - originalEditBottomPanelHeight;
    }

    public void slideDown(ViewGroup view, int height, Direction direction) {
        if (view != null) {
            runSlideDownAnimation(view, 0, height, new DecelerateInterpolator(3));
        }
    }

    private void runSlideUpAnimation(final View view, int translateY, final int height, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                modifySize(view, height);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setArrowDown();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void runSlideDownAnimation(final View view, int translateY, final int height, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setArrowUp();
                modifySize(view, height);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.start();
    }

    private void modifySize(View view, Integer height) {
        view.getLayoutParams().height = height;
        /*
        recyclerView.getLayoutParams().height = height;
        layoutManager.requestLayout();
        */
    }

    private void setArrowUp() {
        slideButton.setImageResource(R.drawable.common_icon_arrow_up_selector);
    }

    private void setArrowDown() {
        slideButton.setImageResource(R.drawable.common_icon_arrow_down_selector);
    }

}
