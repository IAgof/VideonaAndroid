package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.IntroFragment;

/**
 * Created by Veronica Lago Fominaya on 28/07/2015.
 */
public class IntroActivity extends AppIntro2 {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(IntroFragment.newInstance(R.layout.activity_intro_app_1));
        addSlide(IntroFragment.newInstance(R.layout.activity_intro_app_2));
        addSlide(IntroFragment.newInstance(R.layout.activity_intro_app_3));
        addSlide(IntroFragment.newInstance(R.layout.activity_intro_app_4));
        addSlide(IntroFragment.newInstance(R.layout.activity_intro_app_5));

        // OPTIONAL METHODS
        // Override bar/separator color
        /*
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));
        */

        // Hide Skip/Done button
        //showSkipButton(false);
        showDoneButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        //setVibrate(true);
        //setVibrateIntensity(30);

        //setFadeAnimation();
        setCustomTransformer(new ZoomOutPageTransformer());
    }

    /*
    @Override
    public void onSkipPressed() {
        startActivity(new Intent(getApplicationContext(), RecordActivity.class));
    }
    */

    @Override
    public void onDonePressed() {
        startActivity(new Intent(getApplicationContext(), RecordActivity.class));
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
