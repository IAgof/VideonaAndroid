package com.videonasocialmedia.videona.presentation.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;

/**
 * Created by Veronica Lago Fominaya on 02/02/2016.
 */
public class VideonaToast extends Toast {

    public VideonaToast(Context context) {
        super(context);
    }

    public static void showToastBase(Context context, int title, int msg,
                                     int imageId, int duration) {
        new Builder(context)
                .withTitle(title)
                .withMessage(msg)
                .withDrawableImage(imageId)
                .withDuration(duration)
                .build().show();
    }

    /**
     * Builder class for creating different types of toasts
     */
    public static class Builder {

        private Context mContext;
        private int titleToast;
        private int messageToast;
        private int imageToast;
        private int showDuration;


        public Builder(Context context) {
            mContext = context;
        }

        public Builder withTitle(int title) {
            this.titleToast = title;
            return this;
        }

        public Builder withMessage(int message) {
            this.messageToast = message;
            return this;
        }

        public Builder withDrawableImage(int imageId) {
            this.imageToast = imageId;
            return this;
        }

        public Builder withDuration(int duration) {
            this.showDuration = duration;
            return this;
        }


        public VideonaToast build() {

            VideonaToast toast = new VideonaToast(mContext);

            View layout = LayoutInflater.from(mContext).inflate(R.layout.toast_videona, null, false);

            layout.setBackgroundColor(Color.WHITE);

            TextView txtTitle = (TextView) layout.findViewById(R.id.titleToast);
            txtTitle.setText(titleToast);

            TextView txt = (TextView) layout.findViewById(R.id.messageToast);
            txt.setText(messageToast);

            ImageView image = (ImageView) layout.findViewById(R.id.imageToast);
            image.setImageResource(imageToast);

            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(showDuration);
            toast.setView(layout);
            toast.show();

            return toast;

        }
    }
}

