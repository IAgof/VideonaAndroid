/**
 * Widget that lets users select a minimum and maximum value on a given numerical range. The range value types can be one of Long, Double, Integer, Float, Short, Byte or BigDecimal.<br />
 * <br />
 * Improved {@link android.view.MotionEvent} handling for smoother use, anti-aliased painting for improved aesthetics.
 *
 * @param <T> The Number type of the range values. One of Long, Double, Integer, Float, Short, Byte or BigDecimal.
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 */

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 */

package com.videonasocialmedia.videona.presentation.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.videonasocialmedia.videona.R;

import java.math.BigDecimal;

@SuppressLint({"WrongCall", "DrawAllocation", "ViewConstructor"})
public class RangeSeekBar<T extends Number> extends ImageView {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final boolean log = false;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap to paint RangeSeekBar
    private final Bitmap thumbImageRight = BitmapFactory.decodeResource(getResources(), R.drawable.activity_edit_icon_thumb_left_normal);

    //	private final Bitmap point = BitmapFactory.decodeResource(getResources(), R.drawable.point);        
    private final Bitmap lineleft = BitmapFactory.decodeResource(getResources(), R.drawable.activity_edit_icon_thumb_left_normal);
    private final Bitmap lineright = BitmapFactory.decodeResource(getResources(), R.drawable.activity_edit_icon_thumb_right_normal);
    private final float lineHeightHalf = 0.5f * lineleft.getHeight();
    private final float lineWidthHalf = 0.5f * lineleft.getWidth();


    private final float thumbHalfWidth = thumbImageRight.getWidth();
    private final float thumbHalfHeight = 0.5f * thumbImageRight.getHeight();


    private final float lineHeight = thumbHalfHeight;

    private final float padding = thumbImageRight.getWidth();
    private final T absoluteMinValue, absoluteMaxValue;
    private final NumberType numberType;
    private final double absoluteMinValuePrim, absoluteMaxValuePrim;
    private double normalizedMinValue = 0d;
    private double normalizedMaxValue = 1d;
    // amm Octubre
    private double normalizedPointValue = 0d;

    private Thumb pressedThumb = null;
    private boolean notifyWhileDragging = false;
    private OnRangeSeekBarChangeListener<T> listener;

    /**
     * Default color of a  #FF33B5E5.
     */
    //  public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);
    // amm Cambiamos a color por defecto de Android 2.3 RGB R=255 G= 166 B= 0
    //public static final int DEFAULT_COLOR = Color.argb(0xFF, 0xFF, 0xA6, 0x00);
    // amm ahora usamos colores Videona. Rojo Claro: f36161 Rojo Oscuro: 861215
    // public static final int DEFAULT_COLOR = Color.argb(0xFF, 0xF3, 0x61, 0x61);

    // @color/videona_blue #0091EA
    public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x00, 0x91, 0xEA);

    /**
     * An invalid pointer id.
     */
    public static final int INVALID_POINTER_ID = 255;

    // Localized constants from MotionEvent for compatibility
    // with API < 8 "Froyo".
    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00, ACTION_POINTER_INDEX_SHIFT = 8;

    private float mDownMotionX;
    private int mActivePointerId = INVALID_POINTER_ID;

    /**
     * On touch, this offset plus the scaled value from the position of the touch will form the progress value. Usually 0.
     */
    float mTouchProgressOffset;

    private int mScaledTouchSlop;
    private boolean mIsDragging;


    private float numIncrement = 0;

    private int second_increment = 0;

    private Canvas canvas2;


    /**
     * Creates a new RangeSeekBar.
     *
     * @param absoluteMinValue The minimum value of the selectable range.
     * @param absoluteMaxValue The maximum value of the selectable range.
     * @param context
     * @throws IllegalArgumentException Will be thrown if min/max value type is not one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
     */
    public RangeSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context, int numIncrement) throws IllegalArgumentException {
        super(context);

        this.absoluteMinValue = absoluteMinValue;
        this.absoluteMaxValue = absoluteMaxValue;
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);
        this.numIncrement = numIncrement;
        // make RangeSeekBar focusable. This solves focus handling issues in case EditText widgets are being used along with the RangeSeekBar within ScollViews.
        init();
        //onDraw(canvas2);
    }


    private final void init() {
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        // Simulate start on thumb left pressed,
        setNormalizedMinValue(0);
    }

    public boolean isNotifyWhileDragging() {
        return notifyWhileDragging;
    }

    /**
     * Should the widget notify the listener callback while the user is still dragging a thumb? Default is false.
     *
     * @param flag
     */
    public void setNotifyWhileDragging(boolean flag) {
        this.notifyWhileDragging = flag;
    }

    /**
     * Returns the absolute minimum value of the range that has been set at construction time.
     *
     * @return The absolute minimum value of the range.
     */
    public T getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    /**
     * Returns the absolute maximum value of the range that has been set at construction time.
     *
     * @return The absolute maximum value of the range.
     */
    public T getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }


    /**
     * Returns the currently selected min value.
     *
     * @return The currently selected min value.
     */
    public T getSelectedMinValue() {
        return normalizedToValue(normalizedMinValue);
    }


    /**
     * Sets the currently selected minimum value. The widget will be invalidated and redrawn.
     *
     * @param value The Number value to set the minimum value to. Will be clamped to given absolute minimum/maximum range.
     */
    public void setSelectedMinValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMinValue(0d);
        } else {
            setNormalizedMinValue(valueToNormalized(value));
        }
    }

    /**
     * Returns the currently selected max value.
     *
     * @return The currently selected max value.
     */
    public T getSelectedMaxValue() {
        return normalizedToValue(normalizedMaxValue);
    }

    /**
     * Sets the currently selected maximum value. The widget will be invalidated and redrawn.
     *
     * @param value The Number value to set the maximum value to. Will be clamped to given absolute minimum/maximum range.
     */
    public void setSelectedMaxValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMaxValue(1d);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    /**
     * Registers given listener callback to notify about changed selected values.
     *
     * @param listener The listener to notify about changed selected values.
     */
    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
            return false;

        int pointerIndex;

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Down");

                // Remember where the motion event started
                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownMotionX = event.getX(pointerIndex);

                pressedThumb = evalPressedThumb(mDownMotionX);

                // Only handle thumb presses.
                if (pressedThumb == null)
                    return super.onTouchEvent(event);

                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();

                break;
            case MotionEvent.ACTION_MOVE:

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Move");

                if (pressedThumb != null) {

                    if (mIsDragging) {
                        trackTouchEvent(event);
                    } else {
                        // Scroll to follow the motion event
                        pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float x = event.getX(pointerIndex);

                        if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                            setPressed(true);
                            invalidate();
                            onStartTrackingTouch();
                            trackTouchEvent(event);
                            attemptClaimDrag();
                        }
                    }

                    if (notifyWhileDragging && listener != null) {
                        listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());

                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Up");

                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }

                pressedThumb = null;
                invalidate();
                if (listener != null) {
                    listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                    onDraw2();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Pointer_Down");

                final int index = event.getPointerCount() - 1;
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index);
                mActivePointerId = event.getPointerId(index);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Pointer_Up");

                onSecondaryPointerUp(event);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:

                // Log.d(LOG_TAG, "RangeSeekBar onTouchEvent Action_Cancel");

                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    private final void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose
            // a new active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mDownMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private final void trackTouchEvent(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        final float x = event.getX(pointerIndex);

        if (Thumb.MIN.equals(pressedThumb)) {
            setNormalizedMinValue(screenToNormalized(x));
        } else if (Thumb.MAX.equals(pressedThumb)) {
            setNormalizedMaxValue(screenToNormalized(x));
        }

    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * This is called when the user has started touching this widget.
     */
    void onStartTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * This is called when the user either releases his touch or the touch is canceled.
     */
    void onStopTrackingTouch() {
        mIsDragging = false;
    }

    /**
     * Ensures correct size of the widget.
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 300;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = thumbImageRight.getHeight();
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    public void onDraw2() {
        super.onDraw(canvas2);
    }

    /**
     * Draws the widget on the given canvas.
     */
    @SuppressLint("ResourceAsColor")
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw seek bar background lineleft

        //amm  final RectF rect = new RectF(0, 0, getWidth() - padding, getHeight());

        final RectF rect = new RectF(0, 0, getWidth(), getHeight());


        //paint.setColor(R.color.videona_blue_1);
        paint.setColor(Color.WHITE);
        //paint.setColor(Color.LTGRAY);
        paint.setAlpha(200);
        paint.setAntiAlias(true);

        //    canvas.drawRect(rect, paint);

        //get positions of thumbs
        float min_thumb = (int) normalizedToScreen(normalizedMinValue);
        float max_thumb = (int) normalizedToScreen(normalizedMaxValue);


        // draw seek bar active range lineleft (videona color blue)
        rect.left = padding;
        // rect.left = 0;
        rect.right = min_thumb;

        canvas.drawRect(rect, paint);

        //rect.right = max_thumb;


        // pendiente

        //rect.left = Math.min(max_thumb, min_thumb);
        rect.left = max_thumb;
        rect.right = getWidth() - padding;
        // rect.right = getWidth();


        // paint.setColor(R.color.alvaro_green);
        // paint.setColor(Color.LTGRAY);
        // paint.setAlpha(200);
        // paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);

        canvas2 = canvas;

        //draw thumbs
        drawThumbLeft(min_thumb, Thumb.MIN.equals(pressedThumb), canvas);

        drawThumbRight(max_thumb, Thumb.MAX.equals(pressedThumb), canvas);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Overridden to save instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar widget using the {@link #setId(int)} method. Other members of this class than the normalized min and max values don't need to be saved.
     */
    @Override
    protected Parcelable onSaveInstanceState() {

        // Log.d(LOG_TAG, "bundle onSaveInstanceState");

        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", normalizedMinValue);
        bundle.putDouble("MAX", normalizedMaxValue);
        bundle.putDouble("POINT", normalizedPointValue);
        return bundle;
    }

    /**
     * Overridden to restore instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar widget using the {@link #setId(int)} method.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {

        // Log.d(LOG_TAG, "bundle onRestoreInstanceState");

        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        normalizedMinValue = bundle.getDouble("MIN");
        normalizedMaxValue = bundle.getDouble("MAX");
        normalizedPointValue = bundle.getDouble("POINT");
    }

    /**
     * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
     *
     * @param screenCoord The x-coordinate in screen space where to draw the image.
     * @param pressed     Is the thumb currently in "pressed" state?
     * @param canvas      The canvas to draw upon.
     */
    private void drawThumbRight(float screenCoord, boolean pressed, Canvas canvas) {

        //canvas.drawBitmap(lineright, screenCoord - lineWidthHalf, (float) ((0.5f * getHeight()) - lineHeightHalf), paint);
        canvas.drawBitmap(lineright, screenCoord, (float) ((0.5f * getHeight()) - lineHeightHalf), null);
        postInvalidate();
    }

    private void drawThumbLeft(float screenCoord, boolean pressed, Canvas canvas) {

        //canvas.drawBitmap(lineleft, screenCoord - lineWidthHalf, (float) ((0.5f * getHeight()) - lineHeightHalf), null);
        canvas.drawBitmap(lineleft, screenCoord - (2 * lineWidthHalf), (float) ((0.5f * getHeight()) - lineHeightHalf), null);
        postInvalidate();
    }


    /**
     * Decides which (if any) thumb is touched by the given x-coordinate.
     *
     * @param touchX The x-coordinate of a touch event in screen space.
     * @return The pressed thumb or null if none has been touched.
     */
    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
        boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);


        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;

        } else if (minThumbPressed) {
            result = Thumb.MIN;
            // Log.d(LOG_TAG, "RangeSeekBar evalPressedThumb ThumbMin " + result.toString() + " " + touchX);

        } else if (maxThumbPressed) {
            result = Thumb.MAX;
            // Log.d(LOG_TAG, "RangeSeekBar evalPressedThumb ThumbMax" + result.toString() + " " + touchX);

        }

        return result;
    }

    /**
     * Decides if given x-coordinate in screen space needs to be interpreted as "within" the normalized thumb x-coordinate.
     *
     * @param touchX               The x-coordinate in screen space to check.
     * @param normalizedThumbValue The normalized x-coordinate of the thumb to check.
     * @return true if x-coordinate is in thumb range, false otherwise.
     */
    private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
        // Log.d(LOG_TAG, "RangeSeekBar isInThumbRange " + touchX + " / " + normalizedThumbValue);
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
    }


    /**
     * Sets normalized min value to value so that 0 <= value <= normalized max value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized min value to set.
     */
    public void setNormalizedMinValue(double value) {
        value = roundToClosest(value);//set value on the beginning of second in video
        // amm Nov'14 normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
        normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));

        // test try to move MaxValue if touches in MinValue
        //normalizedMaxValue = Math.min(normalizedMaxValue, normalizedMinValue + (ConfigUtils.MAX_VIDEO_DURATION_MILLIS * (1 / numIncrement)));
        //normalizedMaxValue = Math.min(normalizedMaxValue, normalizedMinValue);

        //normalizedMinValue = Math.max(normalizedMaxValue - (ConfigUtils.MAX_VIDEO_DURATION_MILLIS * (1 / numIncrement)), Math.min(value, normalizedMaxValue));
        // Log.d(LOG_TAG, "setNormalizedMinValue " + value + " normalizedMinValue " + normalizedMinValue);
        invalidate();
    }

    /**
     * Sets normalized max value to value so that 0 <= normalized min value <= value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized max value to set.
     */
    public void setNormalizedMaxValue(double value) {

        value = roundToClosest(value); //set value on the beginning of second in video

        normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));

        //normalizedMaxValue = Math.max(normalizedMinValue, Math.min(normalizedMinValue + (ConfigUtils.MAX_VIDEO_DURATION_MILLIS * (1 / numIncrement)), Math.max(value, normalizedMinValue)));

        // test try to move MinValue if touches in MaxValue
        //normalizedMinValue = Math.max(normalizedMinValue, normalizedMaxValue - (ConfigUtils.MAX_VIDEO_DURATION_MILLIS * (1 / numIncrement)));

        //normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
        // // Log.d(LOG_TAG, "setNormalizedMaxValue " + value + " normalizedMaxValue " + normalizedMaxValue);
        invalidate();
    }

    public double roundToClosest(double value) {
        if (value == 1d || value == 0d) return value;

        return Math.round(value * numIncrement) / numIncrement;

    }

    /**
     * Converts a normalized value to a Number object in the value space between absolute minimum and maximum.
     *
     * @param normalized
     * @return
     */
    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        return (T) numberType.toNumber(absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim));
    }

    /**
     * Converts the given Number value to a normalized double.
     *
     * @param value The Number value to normalize.
     * @return The normalized double.
     */
    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    /**
     * Converts a normalized value into screen space.
     *
     * @param normalizedCoord The normalized value to convert.
     * @return The converted value in screen space.
     */
    private float normalizedToScreen(double normalizedCoord) {
        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
    }

    /**
     * Converts screen space x-coordinates into normalized values.
     *
     * @param screenCoord The x-coordinate in screen space to convert.
     * @return The normalized value.
     */
    private double screenToNormalized(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            return 0d;
        } else {
            double result = (screenCoord - padding) / (width - 2 * padding);
            return Math.min(1d, Math.max(0d, result));
        }
    }

    /**
     * Callback listener interface to notify about changed range values.
     *
     * @param <T> The Number type the RangeSeekBar has been declared with.
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    public interface OnRangeSeekBarChangeListener<T> {
        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, T minValue, T maxValue);

    }

    /**
     * Thumb constants (min and max).
     */
    private static enum Thumb {
        MIN, MAX
    }

    ;

    /**
     * Utility enumaration used to convert between Numbers and doubles.
     *
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    private static enum NumberType {
        LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

        public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
            if (value instanceof Long) {
                return LONG;
            }
            if (value instanceof Double) {
                return DOUBLE;
            }
            if (value instanceof Integer) {
                return INTEGER;
            }
            if (value instanceof Float) {
                return FLOAT;
            }
            if (value instanceof Short) {
                return SHORT;
            }
            if (value instanceof Byte) {
                return BYTE;
            }
            if (value instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
        }

        @SuppressLint("UseValueOf")
        public Number toNumber(double value) {
            switch (this) {
                case LONG:
                    return new Long((long) value);
                case DOUBLE:
                    return value;
                case INTEGER:
                    return new Integer((int) value);
                case FLOAT:
                    return new Float(value);
                case SHORT:
                    return new Short((short) value);
                case BYTE:
                    return new Byte((byte) value);
                case BIG_DECIMAL:
                    return new BigDecimal(value);
            }
            throw new InstantiationError("can't convert " + this + " to a Number object");
        }
    }

}
