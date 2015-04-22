package com.videonasocialmedia.videona.presentation.views.login;

import android.app.Activity;

/**
 * Created by jca on 9/3/15.
 */
public interface RegisterView {
    public void showValidField();
    public void showInvalidField();
    public void showErrorMessage(int errorMessageResource);
    public void navigate(Class <? extends Activity> activity);
}
