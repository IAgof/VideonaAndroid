package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.videonasocialmedia.videona.presentation.views.fragment.IntroApp0Fragment;
import com.videonasocialmedia.videona.presentation.views.fragment.IntroApp1Fragment;
import com.videonasocialmedia.videona.presentation.views.fragment.IntroApp2Fragment;
import com.videonasocialmedia.videona.presentation.views.fragment.IntroApp3Fragment;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

/**
 * Created by Veronica Lago Fominaya on 08/01/2016.
 */
public class IntroAppActivity extends AppIntro {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new IntroApp0Fragment());
        addSlide(new IntroApp1Fragment());
        addSlide(new IntroApp2Fragment());
        addSlide(new IntroApp3Fragment());

        sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setCustomTransformer(new ZoomOutPageTransformer());

        setBarColor(Color.parseColor("#00000000"));
        setSeparatorColor(Color.parseColor("#00000000"));
        showSkipButton(true);
    }

    private void loadMainActivity() {
        editor.putBoolean(ConfigPreferences.FIRST_TIME, false).commit();
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v){
        loadMainActivity();
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
