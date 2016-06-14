package com.videonasocialmedia.videona.presentation.views.customviews;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnRangeSeekBarChangeListener;

public class TrimRangeSeekBarView extends FrameLayout implements View.OnTouchListener {

    private final String TAG = "TrimRangeSeekBarView";
    private final String IMAGE_VIEW_TAG_MIN = "image_view_tag_selected";
    private final String IMAGE_VIEW_TAG_MAX = "image_view_tag_unselected";
    private int minThumbWidth, maxThumbWidth, baseLineWidth, minEdgePosition, maxEdgePosition, minThumbPosition, maxThumbPosition;
    private View view, viewBaseLineSelected, viewBaseLineUnselected, viewBaseLine;
    private FrameLayout.LayoutParams lpSelectedView, lpUnselectedView;
    private ImageView ivMaxThumb, ivMinThumb;
    private ViewGroup viewGroupParent;

    private float barHeight;
    private int backgroundColor;
    private int barColor;
    private int barColorSelected;
    private int thumbImage;
    private int thumbMaxImage;

    private OnRangeSeekBarChangeListener mRangeChangeListener;
    private int xDelta;

    private boolean isVideoInitialized;
    private double initLeftSeekBar;
    private double initRightSeekBar;

    public TrimRangeSeekBarView(Context context) {
        super(context);
    }

    public TrimRangeSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeComponents(context, attrs);
    }

    /**
     * Function to initialize components of the class.
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    private void initializeComponents(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.edit_trim_range_seek_bar, this);
        viewGroupParent = (ViewGroup) findViewById(R.id.parent);
        ivMaxThumb = (ImageView) view.findViewById(R.id.ivMaxThumb);
        ivMinThumb = (ImageView) view.findViewById(R.id.ivMinThumb);
        viewBaseLineSelected = view.findViewById(R.id.viewLineSelected);
        viewBaseLineUnselected = view.findViewById(R.id.viewLine);
        viewBaseLine = view.findViewById(R.id.viewBaseLine);
        lpSelectedView = (FrameLayout.LayoutParams) viewBaseLineSelected.getLayoutParams();
        lpUnselectedView = (FrameLayout.LayoutParams) viewBaseLineUnselected.getLayoutParams();
        viewBaseLineSelected.setLayoutParams(lpSelectedView);
        viewBaseLineUnselected.setLayoutParams(lpUnselectedView);
        ivMaxThumb.setTag(IMAGE_VIEW_TAG_MAX);
        ivMinThumb.setTag(IMAGE_VIEW_TAG_MIN);
        ivMinThumb.setOnTouchListener(this);
        ivMaxThumb.setOnTouchListener(this);
        initializeSeekBarThumbPosition();
        setAttributes(context, attrs);
    }

    /**
     * Function to initialize initial position of seek bar thumb
     */
    private void initializeSeekBarThumbPosition() {
        final ImageView ivMaxThumb = (ImageView) findViewById(R.id.ivMaxThumb);
        ViewTreeObserver observer = ivMaxThumb.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivMaxThumb.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                minThumbWidth = ivMinThumb.getMeasuredWidth();
                maxThumbWidth = ivMaxThumb.getMeasuredWidth();
                baseLineWidth = viewBaseLine.getMeasuredWidth();
                Log.i(TAG, "thumb width is : " + maxThumbWidth + " " + baseLineWidth);
                setBaseLineViewPositions();
                setEdgePositions();
            }
        });
    }

    /**
     * Function to set attributes of the range seek bar.
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangeSeekbar, 0, 0);
        try {
            backgroundColor = typedArray.getColor(R.styleable.RangeSeekbar_background_color, getResources().getColor(R.color.colorTransparent));
            barColor = typedArray.getColor(R.styleable.RangeSeekbar_bar_color, getResources().getColor(R.color.colorPrimaryLight));
            barColorSelected = typedArray.getColor(R.styleable.RangeSeekbar_selected_bar_color, getResources().getColor(R.color.colorDivider));
            barHeight = typedArray.getDimension(R.styleable.RangeSeekbar_bar_height, getResources().getDimension(R.dimen.defaultbarHeight));
            thumbImage = typedArray.getResourceId(R.styleable.RangeSeekbar_thumb_image, R.drawable.button_edit_thumb_seekbar_trim_normal);
            thumbMaxImage = typedArray.getResourceId(R.styleable.RangeSeekbar_thumb_image_max, R.drawable.button_edit_thumb_seekbar_trim_normal);
        } finally {
            typedArray.recycle();
        }
        setBackgroundColor(backgroundColor);
        setBarColor(barColor);
        setBarColorSelected(barColorSelected);
        setBarHeight(barHeight);
        setThumbImage(thumbImage);
        setThumbMaxImage(thumbMaxImage);
    }

    /**
     * Function to set base line view position
     */
    private void setBaseLineViewPositions() {
        setViewMarginFor(viewBaseLine);
        setViewMarginFor(viewBaseLineSelected);
        setViewMarginFor(viewBaseLineUnselected);
    }

    /**
     * Function to set position of seek bar min and max thumb.
     */
    private void setEdgePositions() {
        try {
            FrameLayout.LayoutParams lpBaseLine = (FrameLayout.LayoutParams) viewBaseLine.getLayoutParams();
            minEdgePosition = lpBaseLine.leftMargin - ( minThumbWidth / 2 );
            minThumbPosition = minEdgePosition;
            Log.i(TAG, "minEdgePosition " + minEdgePosition);
            maxEdgePosition = minEdgePosition + baseLineWidth - ( maxThumbWidth );
            maxThumbPosition = maxEdgePosition;
            Log.i(TAG, "maxEdgePosition " + maxEdgePosition);
            if (isVideoInitialized) {
                setPositionFor(ivMinThumb, (int) ( initLeftSeekBar * maxEdgePosition ));
                mRangeChangeListener.setUpdateStartTimeTag();
                setPositionFor(ivMaxThumb, (int) ( initRightSeekBar * maxEdgePosition ));
                mRangeChangeListener.setUpdateFinishTimeTag();
            } else {
                setPositionFor(ivMinThumb, minEdgePosition);
                setPositionFor(ivMaxThumb, maxEdgePosition);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to set the background color of range seek bar
     *
     * @param backgroundColor
     */
    public void setBackgroundColor(int backgroundColor) {
        setColorFor(viewGroupParent, backgroundColor);
    }

    /**
     * Function to set color to seek bar.
     *
     * @param barColor int
     */
    public void setBarColor(int barColor) {
        setColorFor(viewBaseLine, barColor);
        setColorFor(viewBaseLineUnselected, barColor);
    }

    /**
     * Function to set init position of
     */

    /**
     * Function to set bar color of seek bar which is selected.
     *
     * @param barColorSelected int
     */
    public void setBarColorSelected(int barColorSelected) {
        setColorFor(viewBaseLineSelected, barColorSelected);
    }

    /**
     * Function to set bar height for all three views.
     *
     * @param barHeight float
     */
    public void setBarHeight(float barHeight) {
        setHeightFor(viewBaseLine, barHeight);
        setHeightFor(viewBaseLineSelected, barHeight);
        setHeightFor(viewBaseLineUnselected, barHeight);
    }

    /**
     * Function to set thumb image for both indicator min and max
     *
     * @param thumbImage int
     */
    public void setThumbImage(int thumbImage) {
        ivMinThumb.setImageResource(thumbImage);
        ivMaxThumb.setImageResource(thumbImage);
    }

    /**
     * Function to set thumb image for max indicator
     *
     * @param thumbMaxImage int
     */
    public void setThumbMaxImage(int thumbMaxImage) {
        ivMaxThumb.setImageResource(thumbMaxImage);
    }

    /**
     * Function to set view margin for the given view.
     *
     * @param view View
     */
    private void setViewMarginFor(View view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = layoutParams.leftMargin + ( minThumbWidth / 2 );
        layoutParams.rightMargin = layoutParams.rightMargin + ( maxThumbWidth / 2 );
        view.setLayoutParams(layoutParams);
    }

    /**
     * Function to set position for the given image
     *
     * @param imageView  ImageView
     * @param leftMargin int
     */
    private void setPositionFor(ImageView imageView, int leftMargin) {
        try {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.leftMargin = leftMargin;
            imageView.setLayoutParams(layoutParams);
            resetViewPosition(imageView, leftMargin, false);
            viewGroupParent.invalidate();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to set color for a viewgroup
     *
     * @param view            ViewGroup
     * @param backgroundColor int
     */
    private void setColorFor(ViewGroup view, int backgroundColor) {
        view.setBackgroundColor(backgroundColor);
    }

    /**
     * Function to set color for a view.
     *
     * @param view            View
     * @param backgroundColor int
     */
    private void setColorFor(View view, int backgroundColor) {
        view.setBackgroundColor(backgroundColor);
    }

    /**
     * Function to set height for a particular view
     *
     * @param view      View
     * @param barHeight float
     */
    private void setHeightFor(View view, float barHeight) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.height = (int) barHeight;
        view.setLayoutParams(layoutParams);
    }

    /**
     * Function to set views based on the position of thumbs.
     *
     * @param view     View
     * @param position int
     */
    private void resetViewPosition(View view, int position, boolean skipPositionCheck) {
        try {
            if (minEdgePosition <= position && position <= maxEdgePosition) {
                if (skipPositionCheck || ( maxThumbPosition - minThumbPosition ) >= maxThumbWidth) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = position;
                    view.setLayoutParams(layoutParams);
                    if (view.getTag().equals(IMAGE_VIEW_TAG_MAX)) {
                        lpSelectedView.width = position;
                        maxThumbPosition = position;
                        viewBaseLineSelected.setLayoutParams(lpSelectedView);
                    } else if (view.getTag().equals(IMAGE_VIEW_TAG_MIN)) {
                        lpUnselectedView.width = position;
                        minThumbPosition = position;
                        viewBaseLineUnselected.setLayoutParams(lpUnselectedView);
                    }
                    if (mRangeChangeListener != null && isVideoInitialized) {
                        double minPosition = getMinPositionValue();
                        double maxPosition = getMaxPositionValue();
                        mRangeChangeListener.setRangeChangeListener(viewGroupParent, minPosition, maxPosition);
                    }
                } else {
                    if (view.getTag().equals(IMAGE_VIEW_TAG_MAX) && position > maxThumbPosition) {
                        maxThumbPosition = position;
                    } else if (view.getTag().equals(IMAGE_VIEW_TAG_MIN) && position < minThumbPosition) {
                        minThumbPosition = position;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get the min thumb position
     *
     * @return double
     */
    public double getMinPositionValue() {
        float minPosition = ( minThumbPosition * 100.0f ) / baseLineWidth;
        return Math.round(minPosition * 100.0) / 100.0;
    }

    /**
     * Function to get the max thumb position
     *
     * @return double
     */
    public double getMaxPositionValue() {
        float minPosition = ( ( maxThumbPosition + maxThumbWidth ) * 100.0f ) / baseLineWidth;
        return Math.round(minPosition * 100.0) / 100.0;
    }

    public TrimRangeSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeComponents(context, attrs);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int xCoordinate = (int) motionEvent.getRawX();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                xDelta = xCoordinate - layoutParams.leftMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                resetViewPosition(view, ( xCoordinate - xDelta ), false);
                break;
            case MotionEvent.ACTION_MASK:
            case MotionEvent.ACTION_UP:
                checkForThumbView(view);
                break;
            default:
                break;
        }
        viewGroupParent.invalidate();
        return true;
    }

    /**
     * Function to check the positions of the thumbs
     */
    private void checkForThumbView(View view) {
        try {
            if (( maxThumbPosition - maxThumbWidth ) < minThumbPosition) {
                if (view.getTag().equals(IMAGE_VIEW_TAG_MAX)) {
                    resetViewPosition(ivMaxThumb, minThumbPosition + maxThumbWidth, true);
                } else if (view.getTag().equals(IMAGE_VIEW_TAG_MIN)) {
                    resetViewPosition(ivMinThumb, maxThumbPosition - minThumbWidth, true);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to set range listener
     *
     * @param rangeChangeListener OnRangeChangeListener
     */
    public void setOnRangeListener(OnRangeSeekBarChangeListener rangeChangeListener) {
        this.mRangeChangeListener = rangeChangeListener;
    }

    public void setInitializedPosition(double left, double right) {
        isVideoInitialized = true;
        initLeftSeekBar = left;// Math.round(left * 100) / 100.0d;
        initRightSeekBar = right;// Math.round(right * 100) / 100.0d;
    }

}
