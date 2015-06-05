package com.videonasocialmedia.videona.presentation.mvp.views;

import android.app.Activity;

/**
 * Created by jca on 9/3/15.
 */
public interface LoginView {
    public void showError(int errorMessageResource);

    public void navigate(Class<? extends Activity> activity);
}
